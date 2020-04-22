# Financial Services Options Pricing using Blackscholes and Monte Carlo


## More documentaiton to follow



## Compile
This project is setup with Maven


```
mvn package
```

That will create the following jar file in the target directory

```
 blackscholes-0.0.1-SNAPSHOT-jar-with-dependencies.jar
```

I like to rename the jar file to something more reasonable

```
cp blackscholes-0.0.1-SNAPSHOT-jar-with-dependencies.jar blackscholes.jar 
```

Copy the jar to a working directory for testing.  



## Usage:

Usage is simple.  All the parameters are past in.
  

```
java -jar blackscholes.jar <initial price> <strike price> <start time> <final time> <num of paths>
```



If you want to play around with running multiple instances, you can do the following:

```
java -jar blackscholes.jar 10.0 11.0 0.0 12.0 10000

java -jar blackscholes.jar 10.0 11.0 12.0 24.0 10000

java -jar blackscholes.jar 10.0 11.0 24.0 36.0 10000

## and so on...

```


## Some consideration

The model runs the following number of iterations (approximately):

```
	endTime - startTime / 0.001 * 10000

```

The time step is 0.001.  It should be smaller - but that will blow up the memory usage.  





FSI Blackscholes
