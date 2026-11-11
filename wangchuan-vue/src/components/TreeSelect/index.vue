<template>
  <div class="el-tree-select">
    <el-popover
      ref="popover"
      placement="bottom-start"
      trigger="click"
      width="100%"
      v-model="visible"
      :disabled="disabled"
    >
      <el-tree
        ref="tree"
        class="el-tree-select__tree"
        :node-key="nodeKey"
        :data="options"
        :props="treeProps"
        :expand-on-click-node="false"
        :default-expand-all="defaultExpandAll"
        :check-strictly="checkStrictly"
        :highlight-current="true"
        @node-click="onClickNode"
      />
      <el-input
        slot="reference"
        ref="input"
        v-model="label"
        :size="size"
        :disabled="disabled"
        :placeholder="placeholder"
        :readonly="true"
        :validate-event="false"
      />
    </el-popover>
  </div>
</template>

<script>
export default {
  name: 'TreeSelect',
  props: {
    value: {
      type: [String, Number],
      default: ''
    },
    options: {
      type: Array,
      default: () => []
    },
    nodeKey: {
      type: String,
      default: 'id'
    },
    props: {
      type: Object,
      default: () => ({ label: 'label', children: 'children' })
    },
    placeholder: {
      type: String,
      default: '请选择'
    },
    size: {
      type: String,
      default: 'medium'
    },
    disabled: {
      type: Boolean,
      default: false
    },
    defaultExpandAll: {
      type: Boolean,
      default: false
    },
    checkStrictly: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      visible: false,
      label: ''
    }
  },
  computed: {
    treeProps() {
      return Object.assign({ label: 'label', children: 'children' }, this.props)
    }
  },
  watch: {
    value: {
      handler(val) {
        this.setLabelByValue(val)
      },
      immediate: true
    }
  },
  methods: {
    onClickNode(node) {
      this.visible = false
      this.$emit('input', node[this.nodeKey])
      this.label = node[this.treeProps.label]
    },
    setLabelByValue(val) {
      if (!val && val !== 0) {
        this.label = ''
        return
      }
      const find = (list) => {
        for (const item of list) {
          if (item[this.nodeKey] === val) {
            this.label = item[this.treeProps.label]
            return true
          }
          if (item[this.treeProps.children] && item[this.treeProps.children].length) {
            if (find(item[this.treeProps.children])) return true
          }
        }
        return false
      }
      find(this.options)
    }
  }
}
</script>

<style lang="scss" scoped>
.el-tree-select {
  width: 100%;
  .el-tree-select__tree {
    max-height: 240px;
    overflow-y: auto;
  }
}
</style>
