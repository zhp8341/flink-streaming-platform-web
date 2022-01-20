import request from '@/utils/request'
import Qs from 'qs'

/**
 * 查询告警日志列表
 * @param {*} pageNum
 * @param {*} pageSize
 * @param {任务编号} jobConfigId
 * @param {任务状态} status
 * @returns
 */
export function alartLogList(pageNum, pageSize, jobConfigId, status) {
  return request({
    url: '/alartLogList',
    method: 'post',
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
    transformRequest: [function(data) { return Qs.stringify(data) }],
    data: {
      pageNum: pageNum,
      pageSize: pageSize,
      jobConfigId: jobConfigId,
      status: status
    }
  })
}

/**
 * 查看错误日志详情
 * @param {*} id
 * @returns
 */
export function logErrorInfo(id) {
  return request({
    url: '/logErrorInfo',
    method: 'post',
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
    transformRequest: [function(data) { return Qs.stringify(data) }],
    data: {
      id: id
    }
  })
}
