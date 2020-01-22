document.addEventListener('DOMContentLoaded', function() {
    var sidenavs = document.querySelectorAll('.sidenav');
    var instances = M.Sidenav.init(sidenavs, {});

    var dropdowns = document.querySelectorAll('.dropdown-trigger');
    var instances = M.Dropdown.init(dropdowns, {constrainWidth: false});
  });