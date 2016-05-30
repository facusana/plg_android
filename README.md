# Perlagloria 2.3.1
This project is based on building part of a mobile app for amateur sport players.
The app should display a set of data to the users (players). In this part we need the following requirements built:
1. Favorite team selection
The player should be able to select his/her favorite team through 3 related combo boxes: A. Championship, B. Tournament, C. Division and then select the team.
The favorite team should be stored in the phone application and must persist over time even if the user closes the application.

2. Show "Next Match" information
The app should show the player the information of the upcoming game his/her team is going to play: the opponent, time and number of field where he/she will play. This information will be accessed via a JSON type WebService to Back-End (Back-End is already developed).

3. Map of the fields
The app should display a map of the fields when the user clicks on the number of field of the upcoming game.

4. Positions table
The app should show the positions and teams of the division where the favorite team plays. (The Back-End already developed).

==========================================================================================================================================================================================
==========================================================================================================================================================================================

SERVER API:

1- GetCustomers
Context: It's used in the first screen when the user is choosing its team.
Notes: Here you only have to put the name of the customer in the screen. 
Request: http://perlagloria.com/customer/getcustomers

2- GetTournaments
Context: In the first screen once the customer has been selected, you have to use this WebService to get all the tournaments by customer id.
Notes: If you want to get data, use the customer "Pasion Eventos". 
Request: http://perlagloria.com/tournament/gettournaments?customerId=20

3- GetDivisions
Context: once the tournament has been selected, you have to use this WebService to get all the divisions by tournament id.
Notes: use the tournament "Robles".
Request: http://perlagloria.com/division/getdivisions?tournamentId=59

4- GetTeams
Context: once the division has been selected, use this one to get all teams by division id.
Notes: Here once the user selects his favorite team, you have to store it in android because you are going to use it when the user enters to the app and show the "Mi equipo" screen.
Request: http://perlagloria.com/team/getteams?divisionId=109

5- GetFixtureMatch
Context: When the user clicks on "Estadisticas" button, you have to use this WebService. 
Notes: 
The request parameter teamId is the one that you stored in android.
You have to put the HomeTeam on the left side of the screen and the AwayTeam on the right side. 
This WebService always returns one object.
You can get the date of the match from 'fixtureDate.date' (in the example is 2015-10-17).
You can get the time of the match from 'hour' (in the example is 16:00:00)
You can get the field number from 'fieldNumber' (in the example is 'M11 2' and in the mock-up is 'Cancha 7')
Request: http://perlagloria.com/fixturematch/getnextfixturematch?teamId=9

6- GetPositionsTeam
Context: When the user clicks on the positions tab. This screen must be scrollable.
Notes: Do not change the order of the list.
You have to calculate the number of the list (the number that is on the left of the team name).
The 'Pts' value is 'points'
The 'PG' value is 'wins'
The 'PE' value is 'ties'
The 'PP' value is 'losses'
The 'GF' value is 'goalsFor'
The 'GC' value is 'goalsAgainst'
Request: http://perlagloria.com/team/getpositionsteams?teamId=9
