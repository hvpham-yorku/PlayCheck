# Team Members: 
- Marwan Al-Jaufy
- Trinity Shiloh Coward
- Khalid Omar Hussein Ali
- Zaid Shahid
- Anthony Zhang

# Meeting Minutes 
### ***Date: Feb 17th - Time: 4:30 pm***
- The primary goals of this meeting were to review the completed work for Iteration 1 and to begin the planning phase for Iteration 2.

- Iteration 0 & 1 Retrospective
The team engaged in a discussion regarding the challenges encountered during the completion of tasks for Iteration 0 and Iteration 1. This was followed by a brief, high-level review of the Iteration 1 deliverables to ensure alignment with project goals.

- Iteration 2: Feature Focus & Planning
Core Feature Selection: The team decided that the primary focus for Iteration 2 will be the creation and organization feature of the application.

- Alignment Check: A discussion was held to confirm that the tasks completed in Iteration 1 provide the necessary foundation for the features planned for Iteration 2.

- Process Improvement: To streamline communication and ensure a more convenient flow of information, the team agreed to document more detailed discussions and decisions directly within Jira for better traceability.
  
- Workflow Optimization: The team brainstormed strategies to improve the division and assignment of tasks among members for future work.

- Immediate Task Assignment:
Specific tasks were assigned to team members relating to two immediate needs:
The planning stage of Iteration 2 and 
preparing to demo the work completed in Iteration 1 to the rest of the team.

- Next Steps & Future Meetings:
A follow-up meeting has been scheduled for February 19th, 2026, to continue discussions regarding the Iteration 1 demo and to finalize any outstanding planning details.

<br> 

### ***Date: Feb 19th - Time: 9:00 pm*** 
- Iteration 1 Review & Task Follow-up: 
The meeting commenced with a review of the tasks assigned during the planning stage of Iteration 1, specifically regarding the demo deliverables. The team discussed the current status of these items and addressed feedback received on completed work, notably the Project Class UML Diagram.

- Decision: To accommodate the finalization of outstanding tasks and the implementation of any required revisions, a two-day extension has been granted. All related work must be completed by [Insert New Deadline].

- Deliverable 1 (Presentation Preparation): 
The team consolidated and agreed upon a final To-Do List that must be completed prior to beginning work on the presentation slides for Deliverable 1.
A detailed discussion was held regarding the structure of the presentation, including the sequence of topics and presenter expectations. Key deadlines for individual contributions leading up to the presentation date were established.

- Client Interview: Video Update
A specific segment of the meeting was dedicated to tracking the progress of the required client interview video. The team emphasized the importance of this deliverable and reviewed the current status with the member assigned to this task.


- Iteration 2: Planning & Task Allocation
The primary focus of the meeting shifted to the planning of Iteration 2.

- Development Strategy: The team agreed on a strategy to split into two functional groups to optimize workflow. One group will focus on front-end development tasks, while the other will concentrate on back-end development.The team also decided to eliminate the organizer as a user in the app and place the responsibilities and capabilities to the Captain of a team.  In that, a captain of a team will only be the user who has the ability to create/organize an event(Game) between two teams. This was agreed upon by the team.

- Task Assignment: A revision to the prepared class diagram was set forth and that  responsibility was handed to Marwan Al-Jaufy. Specific tasks for the iteration were discussed and assigned to team members based on the new front-end/back-end structure.

- Sprint Initialization: Trinity Shiloh  was designated to initialize the new sprint for Iteration 2 in Jira. This individual is responsible for populating the backlog with the required tasks necessary to complete the sprint successfully. Subtask creation and completion  is the responsibility of the person assigned to the main tasks.

<br> 

### ***Date: Mar 3th - Time: 10:30 pm*** 
Iteration 2: Reallocating Tasks 
- After the presentation and reflecting on our work, we decided to focus on our software architecture while continuing to implement user stories. We agreed on the following general software structure for PlayCheck 
+ Presentation Layer:
  + res -> layout (for UI)
  + activityfiles (aggregator of UI and data) 
+ Business Logic:
  + puremodel
  + activityfiles (aggregator of UI and data) 
+ Persistence Layer
  + dataBaseLinkFiles 

<br> 

# Rationale Behind Changes on Plan and Big Design Decisions
- Changing Software Architecture:
  - During Iteration 1, we mainly had our database connections setup so that each activity file (app page) defined a database connection. However as we started to create more pages, there was unnecessary repetition with database connections and it was unclear of how database connections should be done so that our work does not conflict. Therefore, we assigned two people to be in charge of generating these database connections so that our code does not conflict and creation of app pages takes less time.

<br>

# Task Assignments  
#### 1. Trinity 
  - Create Database connection classes
  - Create Persistence Layer
  - Created UI and functionality for Create Team Page for Player 
#### 2. Khalid
  - Create UI for Organizer, Player, and Referee Home Pages 
  - Create UI Navigation for Create Game, Game Schedule, and Game List
  - Added Funtionality to Game Details Page 
#### 3. Marwan
  - Assist with Database Connections 
  - Create Categories for Persistence Layer (Player, Referee, Organizer, Team, etc) and create database conneections for those categories 
#### 4. Zaid
  - Organizer options for league and tournament creation
  - Cleaned up Organizer Activity page 
#### 5. Tony
  - Homepage, with connections accessing league pages, user pages, and so on.
  - Create a design foundation for the visuals of the app for everyone to adapt their own pages.


<br>

# Development Tasks per User Story
#### 1. Detailed User Story (PC 2.2): As a player, I want to view match results and opponents so that I can review performance.
- Development Tasks Completed:
  - Update Game List so that it when game is clicked, it leads to the Game Details Page
    - Estimated Time: 2 days
    - Actual Time: 2 days 

#### 2. Detailed User Story (PC 6.1): As an organizer, I want to create games or tournaments so that events can be scheduled.
- Development Tasks Completed:
  - Created Create Game page UI
    - Estimated Time: 4 hours
    - Actual Time: 2 hours
  - Created navigation from Organizer Home Page
    - Estimated Time: 1 day 
    - Actual Time: 2 days
  - Created Database method which will be used to schedule game
    - Estimated Time: 2 days
    - Actual Time: 3 days
  - Implemented Create Team feature so players can create teams
    - Estimated Time: 3 days
    - Actual Time: 2 days

#### 3. Detailed User Story (PC 6.2): As an organizer, I want to invite teams and referees so that participation is coordinated.
- Development Tasks Completed:
  - Created Player, Referee, Organizer database categories
    - Estimated Time: 3 days 
    - Actual Time: 3 days



