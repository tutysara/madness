$(document).ready(function () {
  prettyPrint();
  $(".row-fluid *[rel=tooltip]").tooltip();
  $("nav *[rel=tooltip]").tooltip({placement: "bottom"});
});
