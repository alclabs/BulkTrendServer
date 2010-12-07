<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%--
  ~ Copyright 2010 Automated Logic
  ~
  ~    Licensed under the Apache License, Version 2.0 (the "License");
  ~    you may not use this file except in compliance with the License.
  ~    You may obtain a copy of the License at
  ~
  ~      http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~    Unless required by applicable law or agreed to in writing, software
  ~    distributed under the License is distributed on an "AS IS" BASIS,
  ~    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~    See the License for the specific language governing permissions and
  ~    limitations under the License.
  --%>

<html>
<head>
    <script type="text/javascript" src="jquery-1.4.2.min.js"></script>
    <script type="text/javascript">
        var fields = [
            {check: 'Display Path',     header: 'Display Path',         id: 'displaypath'},
            {check: 'Type',             header: 'Type',                 id: 'type'},
            {check: 'Enabled',          header: 'Enabled',              id: 'enabled'},
            {check: 'Interval',         header: 'Interval (hh:mm:ss)',  id: 'sampleinterval'},
            {check: 'COV',              header: 'COV',                  id: 'cov'},
            {check: 'Buffer Size',      header: 'Buffer Size',          id: 'buffersize'},
            {check: 'Historian',        header: 'Historian',            id: 'historian'},
            {check: 'Trigger',          header: 'Trigger',              id: 'trigger'},
            {check: 'Upload Period',    header: 'Upload Period (hh:mm:ss)',        id: 'uploadtime'},
            {check: 'Lookup ID',        header: 'Lookup ID',            id: 'lookupstring'},
            //{check: '',             header: '',             id: ''},
        ]


    </script>
    <script type="text/javascript" src="search.js"></script>

    <style type="text/css" id="dynstyle">
        .displaypath    { display: table-cell; }
        .type           { display: table-cell; }
        .enabled        { display: table-cell; text-align:center; }
        .sampleinterval { display: table-cell; width:20pt; }
        .cov            { display: table-cell; text-align:center; }
        .buffersize     { display: table-cell; width:20pt;}
        .historian      { display: table-cell; text-align:center; }
        .trigger        { display: table-cell; }
        .uploadtime     { display: table-cell; width:80pt; }
        .lookupstring   { display: table-cell; }

    </style>

    <title>Trend Search</title>
    <style type="text/css">
        #list td {
            border: solid black 1px;
            padding-left: 4px;
            padding-right:4px;
            vertical-align:top;
        }
        #list th {
            padding-left: 4px;
            padding-right:4px;
            vertical-align:top;
        }

        #columns {
            margin-bottom:20px;
        }
        #columns tr *{
            padding-left: 5px;
            padding-right:5px;
        }
        #columns td {
            text-align:center;
        }

        #filter tr td:first-child {
            text-align:right;
        }
    </style>
</head>
<body>
<form method="post" id="settings">
    <div>Search criteria.  Blanks are ignored.</div>
    <table id="filter">
        <tr>
            <td>Search below: </td>
            <td><input type="text" name="path" size="30"> (path like &quot;/blgda/first_floor&quot;)</td>
        </tr>
        <tr>
            <td>Enabled: </td>
            <td><input type="checkbox" value="true" checked="true" name="enabled"></td>
        </tr>
        <tr>
            <td>Historical: </td>
            <td><input type="checkbox" value="true" checked="true" name="historical"></td>
        </tr>
        <tr>
            <td>Interval = </td>
            <td><input type="text" name="fixed_interval"> (minutes)</td>
        </tr>
        <tr>
            <td>Upload Period > </td>
            <td><input type="text" name="upload_greater"> (minutes)</td>
        </tr>
    </table>
    <div><button id="run" type="button">Search</button></div>
    <br>
</form>
<div id="starthidden" style="display:none;">
    <div>Click a lookup ID to open a page that allows the trends for that ID to be queried.</div>
    <br>
    <div><button type="button" id="lookuponly">Lookup ID Only</button></div>
    <table id="columns" cellpadding="0" cellspacing="0">
        <thead>
            <tr>
            </tr>
        </thead>
        <tbody>
            <tr>
            </tr>
        </tbody>
    </table>
    <table cellpadding="0" cellspacing="0" id="list">
        <thead>
        <tr>
        </tr>
        </thead>
        <tbody id="content">
        </tbody>
    </table>
</div>
</body>
</html>