/*
 * Copyright (c) 2010 Automated Logic Corporation
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
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

