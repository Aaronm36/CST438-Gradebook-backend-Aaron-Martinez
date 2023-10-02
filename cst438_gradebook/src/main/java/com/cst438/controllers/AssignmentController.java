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

	@PutMapping("/assignment/{id}")
	public void updateAssignment(@PathVariable("id") int id, @RequestBody AssignmentDTO adto) {
		// check assignment belongs to a course for this instructor
		String instructorEmail = "dwisneski@csumb.edu";  // user name (should be instructor's email)
		Assignment a = assignmentRepository.findById(id).orElse(null);
		if (a==null || ! a.getCourse().getInstructor().equals(instructorEmail)) {
			throw  new ResponseStatusException( HttpStatus.NOT_FOUND, "assignment not found or not authorized "+id);
		}
		a.setDueDate( java.sql.Date.valueOf(adto.dueDate()));
		a.setName(adto.assignmentName());
		assignmentRepository.save(a);
	}

	@PostMapping("/assignment")
	@Transactional
	public void createAssignment(@RequestBody AssignmentDTO adto) {
		// check that course exists and belongs to this instructor
		String instructorEmail = "dwisneski@csumb.edu";  // user name (should be instructor's email)
		Course c = courseRepository.findById(adto.courseId()).orElse(null);
		if (c==null || ! c.getInstructor().equals(instructorEmail)) {
			throw  new ResponseStatusException( HttpStatus.BAD_REQUEST, "course id not found or not authorized "+adto.courseId());
		}
		// create and save assignment.  Return generated id to client.
		Assignment a = new Assignment();
		a.setCourse(c);
		a.setDueDate( java.sql.Date.valueOf(adto.dueDate()));
		a.setName(adto.assignmentName());
		assignmentRepository.save(a);
	}

	@DeleteMapping("/assignment/{id}")
	public void deleteAssignment(@PathVariable("id") int id, @RequestParam("force") Optional<String> force) {
		// check assignment belongs to a course for this instructor
		String instructorEmail = "dwisneski@csumb.edu";  // user name (should be instructor's email)
		Assignment a = assignmentRepository.findById(id).orElse(null);
		if (a==null) {
			return;
		}
		if (! a.getCourse().getInstructor().equals(instructorEmail)) {
			throw  new ResponseStatusException( HttpStatus.FORBIDDEN, "not authorized "+id);
		}
		// does assignment have grades?  if yes, don't delete unless force is specified
		if (a.getAssignmentGrades().size()==0 || force.isPresent()) {
			assignmentRepository.deleteById(id);
		} else {
			throw  new ResponseStatusException( HttpStatus.BAD_REQUEST, "assignment has grades ");
		}
	}
}

