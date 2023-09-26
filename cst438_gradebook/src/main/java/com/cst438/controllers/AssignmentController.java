package com.cst438.controllers;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

import com.cst438.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@CrossOrigin 
public class AssignmentController {
	
	@Autowired
	AssignmentRepository assignmentRepository;
	
	@Autowired
	CourseRepository courseRepository;


	
	@GetMapping("/assignment")
	public AssignmentDTO[] getAllAssignmentsForInstructor() {
		// get all assignments for this instructor
		String instructorEmail = "dwisneski@csumb.edu";  // user name (should be instructor's email)
		List<Assignment> assignments = assignmentRepository.findByEmail(instructorEmail);
		AssignmentDTO[] result = new AssignmentDTO[assignments.size()];
		for (int i=0; i<assignments.size(); i++) {
			Assignment as = assignments.get(i);
			AssignmentDTO dto = new AssignmentDTO(
					as.getId(),
					as.getName(),
					as.getDueDate().toString(),
					as.getCourse().getTitle(),
					as.getCourse().getCourse_id());
			result[i]=dto;
		}
		return result;
	}
	
	// TODO create CRUD methods for Assignment
	@GetMapping("/assignment/{id}")
	public AssignmentDTO getAssignment2(
			@PathVariable("id") int id) {
		Assignment assignments = assignmentRepository.findById(id).orElse(null);
		if (assignments == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Assignment not found." + id );
		} else {
			AssignmentDTO dto = new AssignmentDTO(
					assignments.getId(),
					assignments.getName(),
					assignments.getDueDate().toString(),
					assignments.getCourse().getTitle(),
					assignments.getCourse().getCourse_id());
			return dto;
		}
	}

	@PutMapping("/assignment/changeName/{id}")
	@Transactional
	public AssignmentDTO updateAssignment(@RequestBody Assignment assignment2, @PathVariable("id") int  assignmentId) {

		Assignment assignment = assignmentRepository.findById(assignmentId).orElse(null);
		if (assignment == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Assignment not found. " + assignmentId);
		}

		assignment2.setId(assignment.getId());
		assignment2.setCourse(assignment.getCourse());
		assignment2.getCourse().setCourse_id(assignment.getCourse().getCourse_id());

		assignmentRepository.save(assignment2);
		AssignmentDTO dto = new AssignmentDTO(
				assignment.getId(),
				assignment.getName(),
				assignment.getDueDate().toString(),
				assignment.getCourse().getTitle(),
				assignment.getCourse().getCourse_id());
		return dto;
	}


	@PostMapping("/assignment/{course_id}/new")
	@Transactional
	public AssignmentDTO newAssignment(@RequestBody Assignment assignment, @PathVariable int course_id) {

		String email = "dwisneski@csumb.edu";  // user name (should be instructor's email)
		Course c = courseRepository.findById(course_id).orElse(null);
		if (!c.getInstructor().equals(email)) {
			throw new ResponseStatusException( HttpStatus.UNAUTHORIZED, "Not Authorized. " );
		}

		assignment.setCourse(c);
		assignmentRepository.save(assignment);

		AssignmentDTO dto = new AssignmentDTO(
				assignment.getId(),
				assignment.getName(),
				assignment.getDueDate().toString(),
				assignment.getCourse().getTitle(),
				assignment.getCourse().getCourse_id());
		return dto;
	}

	@DeleteMapping("/assignment/delete/{id}")
	//
	// DElete /assignment/delete/1?force=yes
	@Transactional
	public void deleteAssignment(@RequestParam ("force") Optional<String> force, @PathVariable("id") int id){
		Assignment assignment = assignmentRepository.findById(id).orElse(null);
		if (assignment == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Assignment not found. " + id);
		}

		String email = "dwisneski@csumb.edu";
			Course c = courseRepository.findById(assignment.getCourse().getCourse_id()).orElse(null);
			if (!c.getInstructor().equals(email)) {
				throw new ResponseStatusException( HttpStatus.UNAUTHORIZED, "Not Authorized. " );
			}

			for (Enrollment e: c.getEnrollments()) {
				for (AssignmentGrade ag : e.getAssignmentGrades()) {
					if(force.isPresent() == true) {
						assignmentRepository.delete(assignment);
					} else if (ag.getScore() != null){
						throw new ResponseStatusException( HttpStatus.CONFLICT, " Assignment has Grades " );
					}
				}
			}
	}
}

