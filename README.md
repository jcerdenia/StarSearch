# StarSearch
This is a sample master-detail application that retrieves and displays the results of search query "star" from the iTunes API. 
The master page displays a list of iTunes tracks with minimal info. Clicking on an item opens a new page with more information about that specific item.

<b>Persistence</b><br>
The app uses different methods of data persistence. The model layer consists of a repository which feeds and receives data from the  

Long-term persistence is achieved through Shared Preferences:
<ul>
  <li>The user's choice of how items are sorted (by relevance, title, genre, or price)</li>
  <li>The user's last viewed page — the app always opens with the most recently viewed page before it was last closed. If the app opens with the detail page, the last viewed item is loaded.</li>
  <li>A date in the list header of when the user last visited.</li>
</ul>

In addition to the above, I have implemented a caching system using a local database. Each time data is retrieved from the API, it is saved to the database and retrieved when the device is not connected to the internet.

The app assumes that data from the API can change at any time. My implementation automatically deletes any old items in the database that are not part of a more recently retrieved list, ensuring that the database is always current. Whenever the device is offline, the most recent data is still fed to the UI.

<b>Architecture</b><br>
The app uses the MVVM architectural pattern, which cleanly separates the user interface from the data source and any data manipulation. 
The model layer consists of a repository (TrackFetcher) which decides whether data should be sourced from the iTunes API or the local database. 
This data is fed to the ViewModel layer, which handles all manipulation needed by the UI (e.g., sorting and filtering). 
The View layer only simply reacts to any changes in the data that it observes in the ViewModel, and updates the UI accordingly.

<b>Extras</b><br>
I added options to search and reorder tracks to demonstrate keeping business logic strictly to ViewModel and limiting the data stream that goes into the UI.

Download APK: https://github.com/joshuacerdenia/StarSearch/blob/master/StarSearch.apk
