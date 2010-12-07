/*
 * Copyright 2010 Automated Logic
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

function handleRun() {
    $.post('servlet/search', $('#settings').serialize(), function(data) {
        var content = $('#content').empty()
        content.append(data)
        $('#starthidden').css('display', 'block')
    })
}


$(document).ready(function() {
    $('#run').click(handleRun)  // run button handler

    var checkHeader = $('#columns thead tr')
    var checkBody =   $('#columns tbody tr')
    var listHeader =  $('#list thead tr')

    for (i in fields) {
        var field = fields[i];
        checkHeader.append('<th>'+field.check+'</th>')
        checkBody.append('<td><input type="checkbox" checked="true" id="'+field.id+'"></td>')
        listHeader.append('<th class="'+field.id+'">'+field.header+'</th>')
    }

    // Toggle column visibility
    $('#columns input[type="checkbox"]').click( function() {
        setColumnOption($(this).attr('id'), $(this).attr('checked'))
    })

    $('#lookuponly').click(function() {
        for (i in fields) {
            var field = fields[i].id;
            var state = (field == "lookupstring");
            setColumnOption(field, state)
            setColumnCheckbox(field, state)
        }
    })

});

function setColumnCheckbox(optionId, state) {
    $('#'+optionId).attr('checked', state)
}

function setColumnOption(optionClass, state) {
    var rule = findClass(optionClass)
    if (rule) {
        if (state) {
            rule.style.display = 'table-cell'
        } else {
            rule.style.display = 'none'
        }
    }
}

function findClass(name) {
    var ss = document.styleSheets[0];
    var rules = null;
    rules =  (ss.cssRules) ? ss.cssRules : ss.rules

    for (i in rules) {
        var rule = rules[i]
        if (rule.selectorText == "."+name) {
            return rule;
        }
    }
    return null
}

