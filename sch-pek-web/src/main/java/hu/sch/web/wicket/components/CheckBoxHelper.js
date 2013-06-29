function switchCheckboxes(element) {
    var table, cbs, i;
    table = element;
    while (table.nodeName.toLowerCase() !== 'table') {
        table = table.parentNode;
    }
    cbs = table.getElementsByTagName('tbody')[0].getElementsByTagName('input');
    for (i = 0; i < cbs.length; i++) {
        if (cbs[i].type === 'checkbox') { 
            cbs[i].checked = element.checked;
        }
    }
}
