package com.wangchuan.workflow.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.wangchuan.common.enums.WhetherStatus;
import com.wangchuan.common.utils.DateUtils;
import com.wangchuan.common.utils.SecurityUtils;
import com.wangchuan.common.utils.uuid.IdUtils;
import com.wangchuan.workflow.domain.BizTable;
import com.wangchuan.workflow.domain.BizTableColumn;
import com.wangchuan.workflow.mapper.BizTableColumnMapper;
import com.wangchuan.workflow.mapper.BizTableMapper;
import com.wangchuan.workflow.service.IBizTableService;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

/**
 * 业务表Service业务层处理
 * 
 * @author oa-generator
 */
@Service
public class BizTableServiceImpl implements IBizTableService {
    private final BizTableMapper bizTableMapper;
    private final BizTableColumnMapper bizTableColumnMapper;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public BizTableServiceImpl(BizTableMapper bizTableMapper, BizTableColumnMapper bizTableColumnMapper, DataSource dataSource) {
        this.bizTableMapper = bizTableMapper;
        this.bizTableColumnMapper = bizTableColumnMapper;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public List<BizTable> selectBizTableList(BizTable bizTable) {
        bizTable.setDelFlag(WhetherStatus.NO.getCode());
        return bizTableMapper.selectBizTableList(bizTable);
    }

    @Override
    public BizTable selectBizTableById(String id) {
        BizTable table = bizTableMapper.selectBizTableById(id);
        if (table != null) {
            table.setColumns(bizTableColumnMapper.selectBizTableColumnByTableId(id));
        }
        return table;
    }

    @Override
    public int insertBizTable(BizTable bizTable) {
        bizTable.setId(IdUtils.fastSimpleUUID());
        bizTable.setStatus(WhetherStatus.YES.getCode());
        bizTable.setDelFlag(WhetherStatus.NO.getCode());
        bizTable.setCreateId(SecurityUtils.getUserId());
        bizTable.setCreateBy(SecurityUtils.getLoginUser().getUser().getNickName());
        bizTable.setCreateTime(DateUtils.getNowDate());
        // 先真正建物理表，再落元数据；建表失败则不上元数据，避免不一致
        runDdl(buildCreateSql(bizTable));
        bizTableMapper.insertBizTable(bizTable);
        saveColumns(bizTable);
        return 1;
    }

    @Override
    public int updateBizTable(BizTable bizTable) {
        bizTable.setUpdateTime(DateUtils.getNowDate());
        bizTable.setUpdateId(SecurityUtils.getUserId());
        bizTable.setUpdateBy(SecurityUtils.getLoginUser().getUser().getNickName());
        List<BizTableColumn> oldCols = bizTableColumnMapper.selectBizTableColumnByTableId(bizTable.getId());
        // 物理表不存在则补建，已存在则按字段差异 ALTER（加列/改列）
        if (!tableExists(bizTable.getTableName())) {
            runDdl(buildCreateSql(bizTable));
        } else {
            for (String sql : buildAlterSqls(oldCols, bizTable.getColumns(), bizTable.getTableName())) {
                runDdl(sql);
            }
        }
        bizTableMapper.updateBizTable(bizTable);
        // 字段整体替换：先删后插
        bizTableColumnMapper.deleteBizTableColumnByTableId(bizTable.getId());
        saveColumns(bizTable);
        return 1;
    }

    private void saveColumns(BizTable bizTable) {
        if (bizTable.getColumns() == null) {
            return;
        }
        int sort = 1;
        for (BizTableColumn col : bizTable.getColumns()) {
            col.setId(IdUtils.fastSimpleUUID());
            col.setTableId(bizTable.getId());
            col.setSort(sort++);
            col.setDelFlag(WhetherStatus.NO.getCode());
            bizTableColumnMapper.insertBizTableColumn(col);
        }
    }

    @Override
    public int deleteBizTableByIds(String[] ids) {
        int rows = bizTableMapper.deleteBizTableByIds(ids);
        bizTableColumnMapper.deleteBizTableColumnByTableIds(ids);
        return rows;
    }

    @Override
    public List<BizTable> selectBizTableOptionList() {
        return bizTableMapper.selectBizTableOptionList();
    }

    @Override
    public String generateDdl(String id) {
        BizTable table = selectBizTableById(id);
        if (table == null || table.getColumns() == null || table.getColumns().isEmpty()) {
            return "-- 无字段，无法生成";
        }
        return buildCreateSql(table);
    }

    /* ============ DDL 执行 ============ */

    private boolean tableExists(String tableName) {
        Integer cnt = jdbcTemplate.queryForObject(
                "SELECT COUNT(1) FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = ?",
                Integer.class, tableName);
        return cnt != null && cnt > 0;
    }

    private void runDdl(String sql) {
        try {
            jdbcTemplate.execute(sql);
        } catch (DataAccessException e) {
            throw new RuntimeException("DDL 执行失败：" + e.getMessage() + "\nSQL: " + sql, e);
        }
    }

    private String buildCreateSql(BizTable t) {
        List<BizTableColumn> cols = t.getColumns();
        List<BizTableColumn> pkCols = cols.stream()
                .filter(c -> "1".equals(c.getIsPk())).collect(Collectors.toList());
        long pkCount = pkCols.size();
        List<String> lines = new ArrayList<>();
        for (BizTableColumn c : cols) {
            StringBuilder sb = new StringBuilder("  `").append(c.getColumnName()).append("` ").append(mapDbType(c));
            if ("1".equals(c.getIsPk())) {
                sb.append(" NOT NULL");
                if (pkCount == 1 && ("BIGINT".equals(c.getDbType()) || "INT".equals(c.getDbType()))) {
                    sb.append(" AUTO_INCREMENT");
                }
            }
            sb.append(" COMMENT '").append(esc(c.getComment())).append("'");
            lines.add(sb.toString());
        }
        if (!pkCols.isEmpty()) {
            String pk = pkCols.stream().map(BizTableColumn::getColumnName)
                    .collect(Collectors.joining("`, `"));
            lines.add("  PRIMARY KEY (`" + pk + "`)");
        }
        return "CREATE TABLE `" + t.getTableName() + "` (\n" + String.join(",\n", lines)
                + "\n) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='" + esc(t.getComment()) + "';";
    }

    private List<String> buildAlterSqls(List<BizTableColumn> oldCols, List<BizTableColumn> newCols, String tableName) {
        List<String> sqls = new ArrayList<>();
        if (newCols == null) {
            return sqls;
        }
        Set<String> oldNames = oldCols.stream().map(BizTableColumn::getColumnName).collect(Collectors.toCollection(HashSet::new));
        for (BizTableColumn c : newCols) {
            String def = "`" + c.getColumnName() + "` " + mapDbType(c)
                    + ("1".equals(c.getIsPk()) ? " NOT NULL" : "")
                    + " COMMENT '" + esc(c.getComment()) + "'";
            if (oldNames.contains(c.getColumnName())) {
                sqls.add("ALTER TABLE `" + tableName + "` MODIFY COLUMN " + def);
            } else {
                sqls.add("ALTER TABLE `" + tableName + "` ADD COLUMN " + def);
            }
        }
        return sqls;
    }

    private String esc(String s) {
        return s == null ? "" : s.replace("'", "''");
    }

    private String mapDbType(BizTableColumn c) {
        String len = (c.getLength() == null || c.getLength().isEmpty()) ? null : c.getLength();
        switch (c.getDbType() == null ? "" : c.getDbType()) {
            case "VARCHAR":
                return "VARCHAR(" + (len == null ? "50" : len) + ")";
            case "DECIMAL":
                return "DECIMAL(" + (len == null ? "18,2" : len) + ")";
            case "INT":
                return "INT";
            case "BIGINT":
                return "BIGINT";
            case "DATETIME":
                return "DATETIME";
            case "DATE":
                return "DATE";
            case "TEXT":
                return "TEXT";
            default:
                return c.getDbType() == null ? "VARCHAR(50)" : c.getDbType();
        }
    }
}
