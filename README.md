# CraftDemo
## Intuit Testing Engineering Craft Demonstration Challenge
## Test Plan
### Introduction
This test plan describes strategies for testing Developer's API

### Features To Be Tested
Developer's API is a RESTful API to fetch Sound data from Sound Cloud provided by NASA. Goal is make a GET API call [Sound API](https://api.nasa.gov/planetary/sounds) with various query parameters and validate responses. This is to ensure that API provides meaningful data to Developers.

### API
Details for API can be found [here](https://api.nasa.gov/api.html#sounds).
1. For this demo, we'll use:
 ** [API End Point](https://api.nasa.gov/planetary/sounds) **

 | Parameter | Type | Default | Description |
 |-----------|------|---------|-------------|
 | q | string | None | Search text to filter results |
 | limit | int | 10 | number of tracks to return |
 | api_key | string | DEMO_KEY | api.nasa.gov key for expanded usage |
1. What is supported?
   1. GET API Call is supported
   1. POST APIs is not supported - If called, Server should return Not Supported Exception
   1. http calls should return error - Only Https calls should be supported by Server

### Scope of Testing
1. Functional Tests
   1. A subset of positive tests will be considered as Smoke Test
   1. A Regression or Functional Tests would contain both Positive and Negative test scenarios
1. Performance Test will be performed to determine
   1. Throughput of System (10 requests, 100 requests)
   1. Average Latency at different throughput levels
1. What is not covered in Demo
   1. DELETE / PUT operations are not tested - Server is RATE LIMITING this test - Couldn't lot of tests
   1. Measure Max Throughput of this system
1. Enhancements
   1. Compute 95%, 99% and 99.9% latencies
   1. Nice graph showing throughput vs latencies

### Test Objectives

1. With Synchronous HttpClient
   1. Verify ability to do GET API Call with Query=appollo, Limit=1 and api_key=DEMO_KEY and validate whether response has 1 track
   1. Verify ability to do GET API Call with Query=appollo, Limit=5 and api_key=DEMO_KEY and validate whether response has 5 tracks
   1. Verify ability to do GET API Call with Query=appollo, No Limit and api_key=DEMO_KEY and validate whether response has default 10 tracks
   1. Verify ability to do GET API Call with Query=appollo, Limit=64 and api_key=DEMO_KEY and validate whether response has 64 tacks
   1. Verify ability to do GET API Call with Query=appollo, Limit=100000 and api_key=DEMO_KEY and validate whether response has 64 tracks
   1. Verify ability to do GET API Call with Query=appollo, Limit=0 and api_key=DEMO_KEY and validate whether response has 0 tracks
   1. Verify ability to do GET API Call with No Query, Limit=1 and api_key=DEMO_KEY and validate whether response has all 64 tracks
   1. Verify ability to do GET API Call with No Query, No Limit and api_key=DEMO_KEY and validate whether response has default 10 tracks
1. With Asynchronous HttpClient
   1. Verify ability to do GET API Call with Query=appollo, Limit=1 and api_key=DEMO_KEY and validate whether response has 1 track
   1. Verify ability to do GET API Call with Query=appollo, Limit=5 and api_key=DEMO_KEY and validate whether response has 5 tracks
   1. Verify ability to do GET API Call with Query=appollo, No Limit and api_key=DEMO_KEY and validate whether response has default 10 tracks
   1. Verify ability to do GET API Call with Query=appollo, Limit=64 and api_key=DEMO_KEY and validate whether response has 64 tacks
   1. Verify ability to do GET API Call with Query=appollo, Limit=100000 and api_key=DEMO_KEY and validate whether response has 64 tracks
   1. Verify ability to do GET API Call with Query=appollo, Limit=0 and api_key=DEMO_KEY and validate whether response has 0 tracks
   1. Verify ability to do GET API Call with No Query, Limit=1 and api_key=DEMO_KEY and validate whether response has all 64 tracks
   1. Verify ability to do GET API Call with No Query, No Limit and api_key=DEMO_KEY and validate whether response has default 10 tracks
1. Negative Tests
   1. Verify inability to do POST API Call with Query=appollo, Limit=1 and api_key=DEMO_KEY and validate 404 Error Code is returned
   1. Verify inability to do GET API Call with Http (not Https), No Query, No Limit and api_key=DEMO_KEY and validate 400 Error Code is returned
   1. Verify inability to do GET API Call with No Query, No Limit and No api_key and validate 403 Error Code is returned
   1. Verify inability to do GET API Call with Query=appollo, Limit=1 and No api_key and validate 403 Error Code is returned
   1. Verify inability to do GET API Call with Query=appollo, Limit=1 and api_key=karthi (Invalid API Key) and validate 403 Error Code is returned
   1. Verify inability to do GET API Call with Query=appollo, Limit=1 and api_key=<empty> (Invalid API Key) and validate 403 Error Code is returned
   1. Verify inability to do GET API Call with Query=appollo, Limit=karthi (Invalid) and api_key=DEMO_KEY and validate 500 Error Code is returned
   1. Verify inability to do GET API Call with URI=planets/sounds (Invalid - Expected is planetary not planets), Query=appollo, Limit=1 and api_key=DEMO_KEY and validate 404 Error Code is returned
   1. Verify inability to do GET API Call with URI=planetary/lights (Invalid - Expected is sounds not lights), Query=appollo, Limit=1 and api_key=DEMO_KEY and validate 404 Error Code is returned
1. During this test, found following 2 test cases failed and it's considered as bug in System
   1. Verify inability to do GET API Call with Query=appollo, Limit=-1 and api_key=DEMO_KEY and validate 500 Error Code is returned
   1. Verify inability to do GET API Call with Query=karthi (Invalid), Limit=100 and api_key=DEMO_KEY and validate 500 Error Code is returned
1. While running these tests repeatedly, System started rate limiting further requests from this IP for a period of time
