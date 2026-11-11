import defaultSettings from '@/settings'

const title = defaultSettings.title || '轻量OA工作流'

export default function getPageTitle(pageTitle) {
  if (pageTitle) {
    return `${pageTitle} - ${title}`
  }
  return `${title}`
}
