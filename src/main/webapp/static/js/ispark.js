
// If the inner elem with class 'codeChunkText' is a div, turn it into
// a textarea.
function enableInnerEdit(codeChunkId) {
  
  var elem = document.getElementById(codeChunkId);
  if (null == elem) {
    return; // Nothing to do here.
  }

  var parentElem = elem.parentNode;

  // property is read-only so we replace the element
  if (elem.tagName == 'DIV') {
    // we remove the element from the DOM
    parentElem.removeChild(elem);

    // Add a new text area based on the original elem.
    var textArea = document.createElement("textarea");
    textArea.setAttribute("id", elem.id);
    var innerText = document.createTextNode(elem.innerText);
    textArea.appendChild(innerText);
    parentElem.appendChild(textArea);
    elem = null; // remove any reference to the old element
  }

}
