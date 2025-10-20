import request from '@/utils/request'

// 查询多肽变异列表
export function listMutation(query) {
  return request({
    url: '/medical/mutation/list',
    method: 'get',
    params: query
  })
}

// 查询多肽变异详细
export function getMutation(id) {
  return request({
    url: '/medical/mutation/' + id,
    method: 'get'
  })
}

// 新增多肽变异
export function addMutation(data) {
  return request({
    url: '/medical/mutation',
    method: 'post',
    data: data
  })
}

// 修改多肽变异
export function updateMutation(data) {
  return request({
    url: '/medical/mutation',
    method: 'put',
    data: data
  })
}

// 删除多肽变异
export function delMutation(id) {
  return request({
    url: '/medical/mutation/' + id,
    method: 'delete'
  })
}

export function downloadGraph(id) {
  return request({
    url: '/medical/mutation/downGraph/' + id,
    method: 'post'
  })
}
