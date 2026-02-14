# Team Members: 
- Marwan Al-Jaufy
- Zacharie Charles
- Trinity Shiloh Coward
- Khalid Omar Hussein Ali
- Zaid Shahid
- Anthony Zhang

# Meeting Minutes 

### **Date: Jan 21st - Time: 8:22 pm**  

Spare Lab Time/Drop-In Hours 
- Time that is not spent working on the lab will be used as drop-in hours
- Drop-In hours will be used to touch base and basically have some work time alongside other team members 
- Not mandatory, but encouraged to so we can understand how actively everyone is working and helping each other with whatever is necessary
  
Topic Alteration
- After warnings from the TA and the Prof of the difficulty of having automatic detection utilizing object tracking, we are looking into just widening the scope of the project
- Intramurals companion app with a player side and a referee side.
Referee Side
- This will include a more manual version of the VAR-style idea we had. Whether or not object tracking will be implemented will be worried about later
- Stat tracking, calls and other things to aid referees will be featured
Player Side
- Mostly data driven things like stats or game history
- It was mentioned that we could do a still sports-focused database thing if the previously mentioned changes to our idea is shot down in the approval process
Prof Approval 
- Everyone present agreed with the changes. The final thing we are worried about is just getting approval. If not, then we can see what we can change about the pivot and look into\


### **Date: Jan 29th - Time: 8:30pm - Iteration 0**  
- Name is officially PlayCheck
- Confirmed and Wrote out the Big User Stories
- Detailed User Stories will be individually sent in discord for confirmations
- Worked out Vision Statement, review soon 


### **Date: Feb 5th  - Time: 8:30pm - Iteration 1**  
- We modified a lot of what we did for iteration 0 and were able to further specify the detailed user stories into groups that we felt more comfortable with
- We acknowledge that we need to change our older Vision Statement that matches the group idea that we newly approved 
- Brainstormed a lot of new detailed user stories for the foundational features of the app, with more subject to come
- Laid out which user stories would be achievable on Jira and allowed them to be open for assignment
  
### **Date: Feb 12th  - Time: 8:30pm - Iteration 1**   
- We discussed our progress on the Jira tasks we chose 
- Decided how user data would be stored in our database. 
- Suggestions were made between one another, including how tasks should be done so that branches can be merged smoothly  

# Big Design Changes & Rationale Behind the Changes 
While originally starting off as just a VAR Detection System, we were quickly finding out that the difficulty of achieving that within the confines of the course would be much more complex than expected. During one of the meetings we agreed on a pivot towards a more robust, all-around companion app for community sports. This would be an app for intramurals/community sports that bridges the referees, players and organizers all into one system. <br/>

# Big User Stories  			
- Review Game Footage
  - (Goal) To be able to look at either a recorded video or a live camera feed. 
- View Game/Player History
  - (Goal) To be able to look at a list/database of games they’ve participated in  
- View Upcoming Games
  - (Goal) To have an overall calendar view of upcoming games  
- Referees Communication
  - (Goal) To be able to effectively communicate with referees to delegate games to them  
- Team Database
   - (Goal) To be able to have a visual of standings and any relevant match stats  
- Event Organization
  - (Goal) To be able to communicate with teams and referees and facilitate event  

# PlayCheck Detail User Stories
## (Game Officiating & Video Review)
**ID: PC-1.1**
- Detailed User Story: As a referee, I want to record video footage of a game so that I can review plays when disputes occur. Priority: Very High Estimated Cost (Days): 4

**ID: PC-1.2**
- Detailed User Story: As a referee, I want to replay recorded clips so that I can make accurate decisions. Priority: Very High  Estimated Cost (Days): 3

**ID: PC-1.3**	
- Detailed User Story: As a referee, I want to view a live camera feed when available so that I can confirm calls in real time. Priority: High Estimated Cost (Days): 3

**ID: PC-1.4**	
- Detailed User Story: As a referee, I want clips linked to specific matches so that footage can be retrieved later Priority: High  Estimated Cost (Days): 2

## (Player Match History)
**ID: PC-2.1**
- Detailed User Story: As a player, I want to see a list of matches I participated in so that I can track my league involvement. Priority: Medium Estimated Cost (Days): 	4

**ID: PC-2.2**	
- Detailed User Story:As a player, I want to view match results and opponents so that I can review performance Priority: Medium  Estimated Cost (Days): 3

**ID: PC-2.3**
- Detailed User Story:As a player, I want to see statistics for past games so that I can monitor improvement  Priority: Medium Estimated Cost (Days):3


## (Game Officiating & Video Review)
**ID: PC-3.1**	
- Detailed User Story:As a player, I want to view upcoming matches in a calendar so that I can plan my schedule. Priority: Medium Estimated Cost (Days): 3

**ID: PC-3.2**
- Detailed User Story: As a referee, I want to see games assigned to me so that I can prepare Priority: Medium  Estimated Cost (Days): 3

**ID: PC-3.3**	
- Detailed User Story: As a user, I want each scheduled match to show time, location, and teams so that I have full match details. Priority: Medium Estimated Cost (Days): 2


## (Referee Communication & Assignment)
**ID: PC-4.1**	
- Detailed User Story:As an organizer, I want to assign referees to matches so that games are properly officiated Priority: High Estimated Cost (Days): 3

**ID: PC-4.2**
- Detailed User Story: As an organizer, I want to message referees so that I can send updates or changes   Priority: High Estimated Cost (Days): 3

**ID: PC-4.3**	
- Detailed User Story:As a referee, I want to receive assignment notifications so that I know when I am scheduled Priority: High Estimated Cost (Days): 2


## (League Standings & Team Database)
**ID: PC-5.1**
- Detailed User Story: As a player, I want to view team standings so that I can see league rankings. Priority: High Estimated Cost (Days): 2

**ID: PC-5.2**
- Detailed User Story: As a user,I want to see win/loss records so that league progress is transparent   Priority: High Estimated Cost (Days): 2

**ID: PC-5.3**
- Detailed User Story:As a player, I want to view match or team statistics so that I can evaluate performance Priority: Medium Estimated Cost (Days): 2


## (Event & Tournament Organization)
**ID: PC-6.1**
- Detailed User Story: As an organizer, I want to create games or tournaments so that events can be scheduled Priority: Medium Estimated Cost (Days): 3

**ID: PC-6.2**
- Detailed User Story: As an organizer, I want to invite teams and referees so that participation is coordinated Priority: MediumEstimated Cost (Days): 2

**ID: PC-6.3** 
- Detailed User Story:As an organizer, I want to update schedules or event details so that participants receive accurate information. Priority: Medium Estimated Cost (Days): 2

# Development Tasks for Iteration 1  
1. Game List (Trinity) 
- Description: A view of previous, current and upcoming games as a list
- Smaller Tasks:
  - Create Database with Games folder
  - UI for Each Game
  - Use Single Item View to Create a Reusable List
  - Create filter for Games
- Time Estimate: 3 days
  - Actual Time: 4 days 

2. Detailed Single Game Page (Khalid)
- Description: Detailed overview for a single game from the game list.
- Smaller Tasks: 
  - Create Game domain model class
  - Implement GameRepository stub using ArrayList
  - Design activity_game_details.xml layout
  - Implement GameDetailsActivity logic
  - Connect UI to Game model
  - Write unit tests for Game class
- Time Estimate: 5 days
  - Actual Time: 4 days

3. Referee Account Creation (Marwan) 
- Description: Create the referee account creation page.
-Smaller Tasks: 
  -Create a Referee Class UML diagram - version 0.0.1
  -BackEnd Code(Based on the UML diagram) w/o Database Connection
  -FrontEnd code 
- Time Estimate: 4 days
  - Actual Time 

4. Organizer Account Creation (Zaid) 
- Description: Create the organizer account creation page.
-Smaller Task: 
  -Store strings such as team name, league, as well as the basics: name, birthdate, etc
  -Create OrganizerProfileSetup class.
  -Make sure everything is connected to each other and stored in the firebase 
- Time Estimate: 2 days
  - Actual Time: 3 days 

5. Player Game Schedule (Anthony) 
- Description: Create a calendar view for past and upcoming games.
-Smaller Tasks: 
	-Create the Calendar UI
	- Show Mini List of Game for Chosen Day 
 - Time Estimate: 3 days
   - Actual Time: 5 days 

6. Initial Login/Register Page (Zacharie) 
- Description: Create the login page for all users and registration page. 
- Smaller Tasks:
  -  Research which backend for the page
  -  basic UI
  -  Complete Login and Registration Page
- Time Estimate: 5 days
  - Actual Time: 3 days

