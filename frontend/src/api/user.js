import request from './request'

export const getUserList = (params) => {
  return request({
    url: '/users',
    method: 'get',
    params
  })
}

export const createUser = (data, avatarFile = null) => {
  const formData = new FormData()
  
  // 将用户数据转换为 JSON 字符串并添加到 FormData
  const userData = {
    username: data.username,
    password: data.password,
    name: data.name,
    employeeNo: data.employeeNo,
    dept: data.dept,
    phone: data.phone,
    role: data.role
  }
  formData.append('userData', new Blob([JSON.stringify(userData)], { type: 'application/json' }))
  
  // 如果有头像文件，添加到 FormData
  if (avatarFile) {
    formData.append('avatar', avatarFile)
  }
  
  return request({
    url: '/users',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

export const updateUser = (id, data, avatarFile = null) => {
  const formData = new FormData()
  
  // 将用户数据转换为 JSON 字符串并添加到 FormData
  const userData = {
    name: data.name,
    employeeNo: data.employeeNo,
    dept: data.dept,
    phone: data.phone,
    status: data.status,
    avatar: data.avatar
  }
  formData.append('userData', new Blob([JSON.stringify(userData)], { type: 'application/json' }))
  
  // 如果有头像文件，添加到 FormData
  if (avatarFile) {
    formData.append('avatar', avatarFile)
  }
  
  return request({
    url: `/users/${id}`,
    method: 'put',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
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

export const exportUsers = (keyword = null) => {
  return request({
    url: '/users/export',
    method: 'get',
    params: keyword ? { keyword } : {},
    responseType: 'blob'
  })
}


