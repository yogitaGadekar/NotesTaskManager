package com.shrisai.notesapp

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import androidx.appcompat.app.AlertDialog

class MainActivity : AppCompatActivity() {
    private lateinit var editTextTitle: EditText
    private lateinit var database: SQLiteDatabase
    private lateinit var editTextContent: EditText
    private lateinit var listViewNotes: ListView
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var notesAdapter: ArrayAdapter<String>
    private lateinit var notesList: ArrayList<String>
    private lateinit var notesIdList: ArrayList<Int>

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        editTextTitle = findViewById(R.id.editTextTitle)
        editTextContent = findViewById(R.id.editTextContent)
        listViewNotes = findViewById(R.id.listViewNotes)

        databaseHelper = DatabaseHelper(this)
        notesList = ArrayList()
        notesIdList = ArrayList()

        notesAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, notesList)
        listViewNotes.adapter = notesAdapter

        listViewNotes.onItemLongClickListener = AdapterView.OnItemLongClickListener { _, _, position, _ ->
            val noteId = notesIdList[position]
            showDeleteDialog(noteId)
            true
        }

        loadNotes()

        val buttonSave = findViewById<Button>(R.id.buttonSave)
        buttonSave.setOnClickListener {
            val title = editTextTitle.text.toString().trim()
            val content = editTextContent.text.toString().trim()
            if (title.isNotEmpty() || content.isNotEmpty()) {
                val id = databaseHelper.insertNote(title, content)
                if (id > -1) {
                    editTextTitle.text.clear()
                    editTextContent.text.clear()
                    loadNotes()
                }
            }
        }
    }

    @SuppressLint("Range")
    private fun loadNotes() {
        val cursor = databaseHelper.getAllNotes()
        notesList.clear()
        notesIdList.clear()

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID))
                val title = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_TITLE))
                val content = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_CONTENT))
                notesIdList.add(id)
                notesList.add("$title\n$content")
            } while (cursor.moveToNext())
        }

        cursor.close()
        notesAdapter.notifyDataSetChanged()
    }

    private fun showDeleteDialog(noteId: Int) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Are you sure you want to delete this note?")
        builder.setPositiveButton("Yes") { _: DialogInterface, _: Int ->
            databaseHelper.deleteNote(noteId)
            loadNotes()
        }
        builder.setNegativeButton("No") { dialog: DialogInterface, _: Int ->
            dialog.dismiss()
        }
        builder.create().show()
    }





}
