Integration tests in AccountEndpointIT are using test containers(https://www.testcontainers.org/) and
expect docker environment. Currently mySql is used fo test database, but Oracle XE is
possible(On Mac with M1 processor it's more involved).

