package com.shrisai.notesapp

 data class Note(
     val id: Long = 0,
     val title: String,
     val content: String,
     val timestamp: Long
 )
