# Team Members: 
- Marwan Al-Jaufy
- Trinity Shiloh Coward
- Khalid Omar Hussein Ali
- Zaid Shahid
- Anthony Zhang

# Meeting Minutes 
### ***Date: March 12 - Time: 5:00 pm***
- Iteration 2 Review (What was done right and wrong) and Discussions about roadmap for Iteration 3
The team got together to talk about how Iteration 2 went. There were things that were done correctly, as well as things that could have been better.
The biggest thing talked about was communication.

<br> 

### ***Date: March 19 - Time: 6:00 pm*** 
- Iteration 3 Review and Task allocation: 
More specific tasks were allocated to group members.
Conversations about the direction of the project were held, especially about the GUI and how it was to be presented.
Made it clear who was to work on what.
The importance of communication was reinforced.

<br>

# Task Assignments  
#### 1. Trinity 
  - Create UI and logic for team standings
  - Create UI and logic for win/loss records 
  - Created UI and logic for match or team statistics 
#### 2. Khalid
  - Create UI and logic for video capture
  - Add functionality for to replay recorded clips
  - Create live camera feed
  - Make it so clips can be linked to matches
#### 3. Marwan
  - Create UI and logic for viewing team match results and opponents
  - Be able to view past statistics
#### 4. Zaid
  - Be able to invite referees and players
  - Be able to edit and update schedules/events
#### 5. Anthony
  - Be able to assign referees to matches
  - Be able to message referees
  - Create UI for being able to make tournaments

<br>

# Rationale Behind Changes on Plan and Big Design Decisions
- Changing Software Architecture:
  - After Iteration 2, we noticed how out of place pieces of code looked. We went through them and arranged them to proper layers so it looks cleaner. We then deleted and unneeded classes and neatly added everything that was needed. Our code works much better now. Regarding the design of the app, we went through different designs and voted on the best one. Moving forward with that design, we linked our pages together to create a great looking app. The biggest drive behind our decisions was cleanliness.

<br>

# Development Tasks per User Story
Khalid - Referee Stories (PC-1.1 → PC-1.4)
PC-1.1 – Record video footage
Set up video capture (device/camera API) — 6h
Backend endpoint to store video — 4h
Save video metadata (DB) — 2h
UI (start/stop recording) — 3h
PC-1.2 – Replay recorded clips
Video playback UI — 3h
Fetch video from backend — 2h
Playback controls (pause, seek) — 2h
PC-1.3 – Live camera feed
Integrate live streaming (WebRTC or similar) — 8h
Display live feed in UI — 3h
Handle connection/loading states — 2h
PC-1.4 – Link clips to matches
DB schema for match-video relation — 2h
Backend linking logic — 3h
UI for selecting match — 3h
Anthony – Organizer Stories (PC-4.1, PC-4.2, PC-6.1)
PC-4.1 – Assign referees to matches
DB structure (matches + referees) — 2h
Backend assignment API — 3h
UI (select/assign referee) — 3h


PC-4.2 – Message referees
Messaging DB structure — 2h
Backend messaging API — 4h
UI (send/view messages) — 4h
Basic notifications — 3h
PC-6.1 – Create tournaments
DB schema (games/tournaments) — 3h
Backend creation endpoints — 4h
UI form for event creation — 4h
Zaid – Organizer Stories (PC-6.2, PC-6.3)
PC-6.2 – Invite teams & referees
Invitation DB structure — 2h
Backend invite system — 4h
UI (send/manage invites) — 4h
Accept/decline logic — 3h
PC-6.3 – Update schedules/details
Backend update endpoints — 3h
UI editing forms — 3h
Validation + error handling — 2h
Participant notifications — 3h
Trinity – Game & Team Stories (PC-5.1 → PC-5.3)
PC-5.1 – View team standings
DB query for standings — 3h
Backend endpoint — 2h
UI table display — 3h
PC-5.2 – View win/loss records
Extend DB queries — 2h
Backend logic — 2h
UI integration — 2h



PC-5.3 – View match/team statistics
Stats calculation logic — 4h
Backend API — 3h
UI (charts/tables) — 4h
Marwan – Player Stories (PC-2.2, PC-2.3)
PC-2.2 – View team match results & opponents
Backend query (matches + opponents) — 3h
API endpoint — 2h
UI list view — 3h
PC-2.3 – View past statistics
Backend stats aggregation — 3h
API endpoint — 2h
UI display (graphs/tables) — 3h

