package com.example.learningwebflux.notification.user

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("user")
internal class User(@Id val id: String)