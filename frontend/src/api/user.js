import request from './request'

export const getUserList = (params) => {
  return request({
    url: '/users',
    method: 'get',
    params
  })
}

export const createUser = (data) => {
  return request({
    url: '/users',
    method: 'post',
    data
  })
}

export const updateUser = (id, data) => {
  return request({
    url: `/users/${id}`,
    method: 'put',
    data
  })
}

export const deleteUser = (id) => {
  return request({
    url: `/users/${id}`,
    method: 'delete'
  })
}

export const importUsers = (file) => {
  const formData = new FormData()
  formData.append('file', file)
  return request({
    url: '/users/import',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

export const exportUsers = () => {
  return request({
    url: '/users/export',
    method: 'get',
    responseType: 'blob'
  })
}


