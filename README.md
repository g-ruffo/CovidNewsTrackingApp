# Covid News Tracking App
Built with Java, this Android application allows users to get up-to-date COVID data from their region by parsing the JSON response from multiple news APIs. This project was built without the use of external libraries and utilizes SharedPreferences to save the users bookmarked articles.

You can choose to view:
- Top Covid-19 News Stories
- Latest Covid-19 Case Numbers

# Covid-19 News Fragment
This fragment uses the mediastack.com API to populate a RecyclerView and allows the user to bookmark a given article which is then saved in SharedPreferences.

<img src="./images/mainscreen1.jpg" width="35%">


# Covid-19 Cases Fragment
This fragment uses the opencovid.ca API to populate a RecyclerView with the latest new Covid-19 cases, which is sorted by province. 

<img src="./images/secondscreen.jpg" width="35%">


# Bookmarked Fragment
This fragment loads a list of bookmarked articles which is stored within SharedPreferences. Users can remove individual articles by clicking on the trash icon.

<img src="./images/bookmarkscreen.jpg" width="35%">


# Preferences Fragment
This fragment allows the user to select location and language settings for the articles populated within the Covid-19 News Fragment and can also choose to limit the data shown in the Covid-19 Cases Fragment to a specific province. From this screen you are also able to restore the default preference settings and clear all the saved articles from the Bookmarked Fragment.

<img src="./images/preferencescreen.jpg" width="35%">

## Frameworks
- Android Preferences

## API
- Media Stack
- Open Covid


## Permissions
- Vibrate
- Access Network State
- Internet


## Required OS
Android 5.0 and up.


# Developed By
Grayson Ruffo

      Copyright 2022 Grayson Ruffo
