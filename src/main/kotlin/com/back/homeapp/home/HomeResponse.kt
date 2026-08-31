package com.back.homeapp.home

import java.time.Instant

data class HomeResponse (
   val id: Long?,
   val name: String,
   val identifier: String,
   val timestamp: Instant,

) {}