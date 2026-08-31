package com.back.homeapp.home

import org.springframework.data.jpa.repository.JpaRepository

interface HomeRepository: JpaRepository<Home, Long>{
}