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
  1. RESTful API GET Call is supported
  1. Only https is supported
  1. POST / PUT / DELETE / HEAD APIs are not supported
  1. http calls should return error

### Scope of Testing
1. Functional Tests
  1. A subset of positive tests will be considered as Smoke Test
  1. A Regression or Functional Tests would contain both Positive and Negative test scenarios
1. Load Test will be performed to determine
  1. Throughput of System
  1. Latencies at different throughput levels
  1. Fixed set of load to be used to determine whether performance of system is consistent between builds and releases

