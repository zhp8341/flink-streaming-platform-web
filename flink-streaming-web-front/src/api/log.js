import request from '@/utils/request'
import Qs from 'qs'

/**
 * 查询日志列表
 * @param {*} pageNum
 * @param {*} pageSize
 * @param {Flink任务ID} jobId
 * @param {任务编号} jobConfigId
 * @param {任务名称} jobName
 * @returns
 */
export function logList(pageNum, pageSize, jobId, jobConfigId, jobName) {
  return request({
    url: '/logList',
    method: 'post',
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
    transformRequest: [function(data) { return Qs.stringify(data) }],
    data: {
      pageNum: pageNum,
      pageSize: pageSize,
      jobId: jobId,
      jobConfigId: jobConfigId,
      jobName: jobName
    }
  })
}

/**
 * 查询日志详情
 * @param {日志编号} logid
 * @returns
 */
export function logDetail(logid) {
  return request({
    url: '/logDetail',
    method: 'post',
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
    transformRequest: [function(data) { return Qs.stringify(data) }],
    data: {
      logid: logid
    }
  })
}
