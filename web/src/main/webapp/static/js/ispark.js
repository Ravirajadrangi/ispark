
// resize a textarea to have as many lines as are required
// to hold all the text the user's typed in.
function resizeTextArea(textAreaId) {

  var elem = document.getElementById(textAreaId);
  if (null == elem) {
    return; // No such element?
  }

  var str = elem.value;
  var cols = elem.cols;
  var linecount = 0;
  $A( str.split( "\n" ) ).each( function( l ) {
    linecount += 1 + Math.floor( l.length / cols ); // take into account long lines
  } )

  elem.rows = linecount;
}
