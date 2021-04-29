###Swapi App
##General Approach

Most of this app is boilerplate client/server interaction using Android MVVC via JetPack components.
I discuss some interesting points below, and added quite a number of TODOs.

-- PeopleImageUrlCache in VolleySingleton attempts to prefetch the URL for all known people. If
the cache is populated by the time a Person Detail page is loaded, the URL will be available and the
image will render. Currently just checking onResume() to see if cache needs updated -- nothing fancy

-- A big feature is "Sections". I logically split a Root Object (person, etc) into the Details and the Section.
Detail is all non-list values specific to the object type. Section is the generic groups of lists like starship,
which only provided a link to the full Details of the Section.

I decided that the DetailsFragment shouldn't know exactly what type of Object it is displaying. It
should delegate the display to two separate mechanisms -- one that knows the object type and how to display
the Details for it, and one that knows how to render a list of links to another objects details.
Section is a generic object that only knows the name, type, and link. It's also
used as a placeholder for Root Object I didn't build support for yet.

The app currently only supports People Details (PersonDetailView) so Section is a way to fulfill the requirement of being
able to browse across various Root Objects. If you try to render a Starship, for example, only the basic data will show
which lets you keep moving, and if Starship details is ever implemented -- it will also show.

-- To facilitate the above, I made an abstract "DataHelper" which is subclassed into "PeopleDataHelper"
that knows everything it needs to build a SwapiViewModel and DetailView, and extract Sections. Keeping all
the implementation details out of DetailFragment

-- SectionAdapter... I thought it would be interesting (fun?) to make an adapter that fetches info
about each Section on the fly, similar to how the Volley imageLoader works. This relies on Volleys internal
cache to avoid too many network requests. (Can be confirmed using network tools)

-- I brought in Parcelize, but didn't end up using it. Probably will so I left it. https://developer.android.com/kotlin/parcelize

-- Bonus feature: change from light/dark theme and see how the app looks

##Various ideas to improve the app.

-- Could use a Realm/Room database for persistence between launches and object-level caching. Volley has a fair amount of
URL caching built in but it's low granularity and doesn't support offline. If this was implemented, all the LiveData would be driven from
the database and not directly through NetworkHelper.

-- Switch to GraphQL. I thought it would be less time consuming to switch from regular to GraphQL.

-- Enable fragment backstack. I ran into some late-game bugs with the backstack and the requirements
only mentioned forward navigation so I simply turned off backstack.

-- Improve overall visual design. Didn't test on a variety of screen sizes

-- Better error handling for poor connection situations. (I only did it on the main screen)

-- Build full data models for all objects, not just Person. (Section class is currently a placeholder for
unimplemented models)

-- Search autocomplete

-- I used Master/Detail template, but could improve with Android Navigation library (NavHostFragment)

-- I honestly never tested it on a tablet... I was so caught up in the details and now my tablet is dead
so I just hope that master/detail works as intended. :)

-- Better image server with images at different sizes and resolutions would allow thumbnails in the lists, and such.
Currently impossible with high res images used in detail screens.

-- ViewBinding, maybe

-- Can't really think of good use of Hilt, but perhaps in the future

-- TESTS. LOTS OF TESTS

-- KDoc on more functions, I tried to at least do all the classes

-- Apply PeopleImageUrlCache idea to more than just people, or again, get a better API with deterministic URLs
so we don't have to map it.

-- Could write a Google Cloud function to treat the various image and data sources as microservices so
that the return of a Person object already has all the json images for all the other detail section
you may click on (instead of just URL). This would end up in Realm DB and power LiveData

-- Progress loader does not account for the image load/render time. Could use some feedback there.

