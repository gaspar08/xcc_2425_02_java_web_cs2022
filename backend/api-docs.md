# TodoList API 文档

## 基础信息

- 基础URL: `/api/v1`
- 所有请求和响应均使用JSON格式
- 认证方式：Bearer Token

## 用户认证接口

### 用户注册

```http
POST /auth/register
```

**请求体：**
```json
{
  "username": "string",
  "password": "string",
  "email": "string"
}
```

**响应：**
```json
{
  "code": 200,
  "message": "注册成功",
  "data": {
    "userId": "string",
    "username": "string",
    "email": "string"
  }
}
```

### 用户登录

```http
POST /auth/login
```

**请求体：**
```json
{
  "email": "string",
  "password": "string"
}
```

**响应：**
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "string",
    "userId": "string",
    "username": "string"
  }
}
```


## 待办事项接口

### 获取待办事项列表

```http
GET /todos
```

**请求头：**
- Authorization: Bearer {token}

**查询参数：**
- completed (可选): boolean - 筛选已完成/未完成的待办事项
- page (可选): number - 页码，默认1
- size (可选): number - 每页数量，默认10

**响应：**
```json
{
  "code": 200,
  "data": {
    "total": 0,
    "items": [
      {
        "id": "string",
        "title": "string",
        "completed": false,
        "createdAt": "string",
        "updatedAt": "string"
      }
    ]
  }
}
```

### 创建待办事项

```http
POST /todos
```

**请求头：**
- Authorization: Bearer {token}

**请求体：**
```json
{
  "title": "string"
}
```

**响应：**
```json
{
  "code": 200,
  "message": "创建成功",
  "data": {
    "id": "string",
    "title": "string",
    "completed": false,
    "createdAt": "string",
    "updatedAt": "string"
  }
}
```

### 更新待办事项

```http
PUT /todos/{id}
```

**请求头：**
- Authorization: Bearer {token}

**请求体：**
```json
{
  "title": "string",
  "completed": boolean
}
```

**响应：**
```json
{
  "code": 200,
  "message": "更新成功",
  "data": {
    "id": "string",
    "title": "string",
    "completed": boolean,
    "createdAt": "string",
    "updatedAt": "string"
  }
}
```

### 删除待办事项

```http
DELETE /todos/{id}
```

**请求头：**
- Authorization: Bearer {token}

**响应：**
```json
{
  "code": 200,
  "message": "删除成功"
}
```

## 错误响应

当发生错误时，API将返回以下格式的响应：

```json
{
  "code": number,
  "message": "string",
  "error": "string"
}
```

常见错误码：
- 400: 请求参数错误
- 401: 未认证或认证失败
- 403: 权限不足
- 404: 资源不存在
- 500: 服务器内部错误