package com.techelevator.dao;

import java.time.LocalDate;

import com.techelevator.model.Assignment;

public interface AssignmentDAO {
	//need these for API calls
	//returns list of assignments for courses the user with the given id is taking
	Assignment[] getStudentAssignments(int student);
	//creates a new homework assignment for the given course id on the given date, returns true if successful and false otherwise
	boolean newAssignment(int course, LocalDate date, Assignment homework);
	//returns the assignment with the given id
	Assignment getAssignmentById(int id);
	//submits the homework assignment with the given id on behalf of the student with the given id at the current time, returns true if successful and false otherwise
	boolean submitAssignment(int id, int student);
	//returns the user id of the teacher of the course for which this homework was assigned (harder than it seems, save this for last)
	int getTeacher(int id);
	//adds an answer for the given question on the assignment with the given id from the given student, returns true if successful and false otherwise
	boolean submitAnswer(int id, int question, int student, String answer);
	//deletes a homework assignment, returns true if successful or false otherwise
	boolean deleteAssignment(int id);
	
	Integer getAssignmentId(int assignment);
	LocalDate getDueDate(LocalDate dueDate);
	Integer getQuestions(int questions);
	

}
