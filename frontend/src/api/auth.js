import request from './request'

export const login = (username, password) => {
  return request({
    url: '/auth/login',
    method: 'post',
    data: { username, password }
  })
}

export const getCurrentUser = () => {
  return request({
    url: '/auth/me',
    method: 'get'
  })
}


