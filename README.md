# StarSearch
This is a sample master-detail application that retrieves and displays the results of search query "star" from the iTunes API. 
The master page displays a list of iTunes tracks with minimal info. Clicking on an item opens a new page with more information about that specific item.

<b>Architecture</b><br>
The app uses the Model-ViewModel-View (MVVM) architectural pattern.

<b>Persistence</b><br>
The app uses several methods of data persistence. Short-term persistence is handled by ViewModels. 

Long-term persistence is achieved through SharedPreferences:
<ul>
  <li>The user's choice of how items are sorted (by relevance, title, genre, or price)</li>
  <li>The user's last viewed page — the app always opens with the most recently viewed page before it was last closed.</li>
  <li>The most recently viewed item — if the app opens with the detail page, the last viewed item is loaded.</li>
  <li>A date in the list header of when the user last visited.</li>
</ul>

In addition to the above, I have implemented a caching system using Room.
