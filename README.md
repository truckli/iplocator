
# Optimized 17mon IP Locating Library


Enhanced from the official version <https://github.com/17mon/java> with the following features added:

* I/O accesses are only involved once during initialization, and query operations are totally in-memory, giving fast response time.
* Spark rdd parallel operations are supported, so that a large batch of IP locating queries can be finished in a distributed manner.
* Writen in Scala, both Scala/Java examples are provided.

## Limitations

* Only datx format is supported, which is non-free. That means you or your organization have to purchase the offline database from <http://www.ipip.net>.

## A Java Example:

The following code snippet outputs which nation a give IP address belongs to.

```
IPLocator locator = new IPLocator(fileName);
IPLocation IPregion = locator.locate("182.207.255.255");
System.out.println("nation: " + IPregion.nation());
```


