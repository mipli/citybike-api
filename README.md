# Oslo City Bike API

A simple API wrapper around the official data endpoints for [Oslo City Bike](https://oslobysykkel.no/apne-data/sanntid).

Written using Kotlin, with the Ktor web framework.

Supported features:
 - Get list of all stations, with current bike count and vacant slots. List can be sorted by name, current bike count or vacant slot count.
 - Look up station by name
 - Find closest station based on longitude and latitude

## Running it

Build the Docker file with
```
$ docker build -t letsride .
```

And then run it with 
```
$ docker run -p 8000:8080 letsride
```

The API will then be avaible at `http://localhost:8000`, with the following endpoints:
```
http://localhost:8000/list?sort=name
 -> list bike stations sorted by their name, this is the default sorting if nothing is provided
http://localhost:8000/list?sort=bikes
 -> list bike stations sorted by available bikes
http://localhost:8000/list?sort=vacancies
 -> list bike stations sorted by available open spots
http://localhost:8000/list?sort=pos&lat=59.9294841332649646&lon=10.679715466302344
 -> list bike stations sorted by the given position, ties are sorted by name
```

## Testing it

The tests are run with
```
$ gradle test
```

## Things to improve

- Better error handling and messages if the City Bike API is producing errors
- Should add Swagger API documentation
- Test setup with Koin is not optimal, so there should be better ways for doing that
- No authentication at the moment, so should add JWT or simple API key support
