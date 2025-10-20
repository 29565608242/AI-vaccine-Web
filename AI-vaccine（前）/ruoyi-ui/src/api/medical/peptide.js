import request from '@/utils/request'

// 查询亲和度列表
export function listPeptide(query) {
  return request({
    url: '/medical/peptide/list',
    method: 'get',
    params: query
  })
}

// 查询亲和度详细
export function getPeptide(id) {
  return request({
    url: '/medical/peptide/' + id,
    method: 'get'
  })
}

// 新增亲和度
export function addPeptide(data) {
  return request({
    url: '/medical/peptide',
    method: 'post',
    data: data
  })
}

// 修改亲和度
export function updatePeptide(data) {
  return request({
    url: '/medical/peptide',
    method: 'put',
    data: data
  })
}

// 删除亲和度
export function delPeptide(id) {
  return request({
    url: '/medical/peptide/' + id,
    method: 'delete'
  })
}

export function getData(id) {
  return request({
    url: '/medical/peptide/getData/' + id,
    method: 'get'
  })
}

export function dnaChange(data) {
  return request({
    url: '/medical/peptide/dnaChange',
    method: 'post',
    data: data,
    timeout: 600000
  })
}


export function tcrChange(data) {
  return request({
    url: '/medical/peptide/tcrChange',
    method: 'post',
    data: data,
    timeout: 600000
  })
}
