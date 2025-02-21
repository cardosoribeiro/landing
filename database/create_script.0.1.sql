    -- Create the database named "landing"
    CREATE DATABASE landing;

    -- Use the "landing" database
    USE landing;

    -- Create the "users" table (for authentication)
    CREATE TABLE user (    
        id_user INT AUTO_INCREMENT PRIMARY KEY,  -- Auto-incrementing user ID
        username VARCHAR(50) UNIQUE NOT NULL, -- Username (must be unique)
        password VARCHAR(255) NOT NULL,       -- Password (hashed for security)
        role VARCHAR(20) CHECK( role in ('student','instructor', 'admin'))   -- User role (e.g., 'student', 'instructor', 'admin')
    );


    -- Create the "room" table
    CREATE TABLE room (
        building VARCHAR(15),       -- Building where the room is located
        room_number INT,             -- Room number (integer for efficiency)
        capacity NUMERIC(4, 0),      -- Room capacity
        PRIMARY KEY (building, room_number) -- Rooms are uniquely identified by building and room number
    );

    -- Create the "department" table
    CREATE TABLE department (
        department_name VARCHAR(20),  -- Name of the department
        building VARCHAR(15),          -- Building where the department is located
        budget NUMERIC(12, 2) CHECK (budget > 0), -- Department budget (must be positive)
        PRIMARY KEY (department_name)   -- Departments are uniquely identified by name
    );

    -- Create the "course" table
    CREATE TABLE course (
        course_id VARCHAR(8),        -- Unique course ID
        title VARCHAR(50),           -- Course title
        department_name VARCHAR(20),  -- Department offering the course
        credits NUMERIC(2, 0) CHECK (credits > 0), -- Number of course credits (must be positive)
        PRIMARY KEY (course_id),      -- Courses are uniquely identified by ID
        FOREIGN KEY (department_name) REFERENCES department(department_name) ON DELETE SET NULL -- Links course to department
    );

    -- Create the "instructor" table (WITH id_user column)
    CREATE TABLE instructor (
        instructor_id VARCHAR(5),    -- Unique instructor ID
        name VARCHAR(20) NOT NULL,   -- Instructor name (required)
        department_name VARCHAR(20),  -- Department the instructor belongs to
        salary NUMERIC(8, 2) CHECK (salary > 29000), -- Instructor salary (must be greater than 29000)
        id_user INT UNIQUE,           -- Column to link to users table (unique for instructors)
        PRIMARY KEY (instructor_id),  -- Instructors are uniquely identified by ID
        FOREIGN KEY (department_name) REFERENCES department(department_name) ON DELETE SET NULL, -- Links instructor to department
        FOREIGN KEY (id_user) REFERENCES user(id_user) ON DELETE SET NULL -- Foreign key constraint to users table
    );

    -- Create the "section" table
    CREATE TABLE section (
        course_id VARCHAR(8),        -- Course the section belongs to
        section_id VARCHAR(8),       -- Unique section identifier (e.g., A, B)
        semester VARCHAR(6) CHECK (semester IN ('Fall', 'Winter', 'Spring', 'Summer')), -- Semester the section is offered
        year NUMERIC(4, 0) CHECK (year BETWEEN 1701 AND 2100), -- Year the section is offered
        building VARCHAR(15),         -- Building where the section meets
        room_number INT,             -- Room number where the section meets
        period_id VARCHAR(4),          -- Time period ID (references the "period" table)
        PRIMARY KEY (course_id, section_id, semester, year), -- Sections are uniquely identified by course, section, semester, and year
        FOREIGN KEY (course_id) REFERENCES course(course_id) ON DELETE CASCADE, -- Links section to course
        FOREIGN KEY (building, room_number) REFERENCES room(building, room_number) ON DELETE SET NULL -- Links section to room
    );

    -- Create the "teaches" table
    CREATE TABLE teaches (
        instructor_id VARCHAR(5),    -- Instructor teaching the section
        course_id VARCHAR(8),        -- Course of the section
        section_id VARCHAR(8),       -- Specific section
        semester VARCHAR(6),         -- Semester the section is taught
        year NUMERIC(4, 0),          -- Year the section is taught
        PRIMARY KEY (instructor_id, course_id, section_id, semester, year), -- Uniquely identifies an instructor's teaching assignment
        FOREIGN KEY (course_id, section_id, semester, year) REFERENCES section(course_id, section_id, semester, year) ON DELETE CASCADE, -- Links teaching assignment to section
        FOREIGN KEY (instructor_id) REFERENCES instructor(instructor_id) ON DELETE CASCADE -- Links teaching assignment to instructor
    );

    -- Create the "student" table (WITH id_user column)
    CREATE TABLE student (
        student_id VARCHAR(5),      -- Unique student ID
        name VARCHAR(20) NOT NULL,   -- Student name (required)
        department_name VARCHAR(20),  -- Department the student belongs to
        total_credits NUMERIC(3, 0) CHECK (total_credits >= 0), -- Total credits earned by the student
        id_user INT UNIQUE,           -- Column to link to users table (unique for students)
        PRIMARY KEY (student_id),  -- Students are uniquely identified by ID
        FOREIGN KEY (department_name) REFERENCES department(department_name) ON DELETE SET NULL, -- Links student to department
        FOREIGN KEY (id_user) REFERENCES user(id_user) ON DELETE SET NULL -- Foreign key constraint to users table
    );

    -- Create the "takes" table
    CREATE TABLE takes (
        student_id VARCHAR(5),      -- Student taking the section
        course_id VARCHAR(8),        -- Course of the section
        section_id VARCHAR(8),       -- Specific section
        semester VARCHAR(6),         -- Semester the student took the section
        year NUMERIC(4, 0),          -- Year the student took the section
        grade CHAR(2) CHECK (grade IN ('A+', 'A-', 'B+', 'B-', 'C+', 'C-', 'D+', 'D-', 'F+', 'F-')), -- Student's grade in the section
        PRIMARY KEY (student_id, course_id, section_id, semester, year), -- Uniquely identifies a student's enrollment in a section
        FOREIGN KEY (course_id, section_id, semester, year) REFERENCES section(course_id, section_id, semester, year) ON DELETE CASCADE, -- Links enrollment to section
        FOREIGN KEY (student_id) REFERENCES student(student_id) ON DELETE CASCADE -- Links enrollment to student
    );

    -- Create the "mentor" table
    CREATE TABLE mentor (
        student_id VARCHAR(5),      -- Student being mentored
        instructor_id VARCHAR(5),    -- Instructor acting as mentor
        PRIMARY KEY (student_id),  -- A student can have only one mentor (or at least, this schema enforces that)
        FOREIGN KEY (instructor_id) REFERENCES instructor(instructor_id) ON DELETE SET NULL, -- Links mentorship to instructor
        FOREIGN KEY (student_id) REFERENCES student(student_id) ON DELETE CASCADE -- Links mentorship to student
    );

    -- Create the "prerequisite" table
    CREATE TABLE prerequisite (
        course_id VARCHAR(8),        -- Course that has a prerequisite
        prerequisite_id VARCHAR(8),   -- ID of the prerequisite course
        PRIMARY KEY (course_id, prerequisite_id), -- A course can have multiple prerequisites
        FOREIGN KEY (course_id) REFERENCES course(course_id) ON DELETE CASCADE, -- Links course to its prerequisites
        FOREIGN KEY (prerequisite_id) REFERENCES course(course_id) ON DELETE CASCADE -- Links prerequisite to course
    );

    -- Create the "period" table
    CREATE TABLE period (
        period_id VARCHAR(4),        -- Unique period ID (e.g., M1, T2)
        day CHAR(1) CHECK (day IN ('M', 'T', 'W', 'R', 'F', 'S', 'U')), -- Day of the week
        start_time TIME,             -- Start time of the period
        end_time TIME,               -- End time of the period
        PRIMARY KEY (period_id, day, start_time)  -- Periods are uniquely identified by ID, day and start time.  Consider if 'day' should be part of the PK
    );

