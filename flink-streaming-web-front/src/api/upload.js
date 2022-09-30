import request from '@/utils/request'
import Qs from 'qs'

export function queryUploadFile(pageNum, pageSize, fileName) {
  return request({
    url: '/queryUploadFile',
    method: 'post',
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
    transformRequest: [function(data) { return Qs.stringify(data) }],
    data: {
      pageNum: pageNum,
      pageSize: pageSize,
      fileName: fileName
    }
  })
}
export function deleteFile(id) {
  return request({
    url: '/deleteFile',
    method: 'post',
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
    transformRequest: [function(data) { return Qs.stringify(data) }],
    data: {
      id: id
    }
  })
}


