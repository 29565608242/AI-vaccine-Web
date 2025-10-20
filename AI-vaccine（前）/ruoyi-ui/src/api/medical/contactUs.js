import request from '@/utils/request'

// 查询联系我们列表
export function listContactUs(query) {
  return request({
    url: '/medical/contactUs/list',
    method: 'get',
    params: query
  })
}

// 查询联系我们详细
export function getContactUs(id) {
  return request({
    url: '/medical/contactUs/' + id,
    method: 'get'
  })
}

// 新增联系我们
export function addContactUs(data) {
  return request({
    url: '/medical/contactUs',
    method: 'post',
    data: data
  })
}

// 修改联系我们
export function updateContactUs(data) {
  return request({
    url: '/medical/contactUs',
    method: 'put',
    data: data
  })
}

// 删除联系我们
export function delContactUs(id) {
  return request({
    url: '/medical/contactUs/' + id,
    method: 'delete'
  })
}
