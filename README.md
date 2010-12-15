Purpose
=======

The BulkTrendServer Add-On to WebCTRL provides a REST style web service to retrieve trend samples more efficiently than the built in SOAP access.  This Add-On is intended for use by a programmer who wants to retrieve trends remotely in an efficient manner.

Branches
--------
A description of the different branches is in [BRANCHES.md](blob/master/BRANCHES.md).

Use
---
 Data should be retrieved using an HTTP Post request to a URL of webctrl-server/trendserver/read. After installing the BulkTrendServer add-on, the documentation will be available at the /trendserver URL on your WebCTRL site. The source for
the documentation is in this project at src/main/webapp/doc.html.

 See alclabs Bulk Trend Client tool project for a sample java application that can retrieve data from this add-on.


Support and Other Downloads
---------------------------

The [ALCshare.com](http://www.alcshare.com) site should be used for discussion or support for this add-on.  Binaries for this and other add-ons are available there.