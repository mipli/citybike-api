# Oslo City Bike API

A simple API wrapper around the official data endpoints for [Oslo City Bike](https://oslobysykkel.no/apne-data/sanntid).

Written using Kotlin, with the Ktor web framework.

Supported features:
 - Get list of all stations, with current bike count and vacant slots. List can be sorted by name, current bike count or vacant slot count.
 - Look up station by name
 - Find closest station based on longitude and latitude
