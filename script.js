$(document).ready(function() {
  
  jQuery.fx.interval = 5;
  
  var html = $("html");
  var mainContainer = $("#mainContainer");
  
  $(document).on("click","#solverLink, #reset", function() {
    mainContainer.animate({opacity:"0"}, function() {
      mainContainer.load("SolverMainContainer.html", function() {
        mainContainer.animate({opacity:"1"});
        
        $("#form").validate({
          
          onfocusout: false,
          onkeyup: false,
          
          errorPlacement: function() {
            return true;
          },
          highlight: function(element) {
            $(element).animate({backgroundColor:"rgba(250, 192, 144, 1)"})
                      .animate({backgroundColor:"rgba(250, 192, 144, 0.01)"});
          },
          
          submitHandler: function() {
            
            var animateOnSubmit = $("#animateOnSubmit");
            var loadingGif = $("#loadingGif");
            
            html.css("overflow","hidden");
            animateOnSubmit.animate({top:"-=10px"})
                           .animate({top:"+=350px", opacity:"0"}, function() {
              loadingGif.css("display","inline").animate({opacity:"1"});
            });
            
            var formData = $("#form").serialize();
            $.ajax({
              type: "GET",
              url: "/solve",
              data: formData,
              cache: false,
              
              success: function(data) {
                loadingGif.fadeOut(function() {
                  animateOnSubmit.html(data).animate({top:"-=350px", opacity:"1"})
                                            .animate({top:"+=10px"}, function() {
                    html.css("overflow","auto");
                  });
                });
              }
            });
            
            return false;
          }
        });
      });
    });
  });
  
  $(document).on("click","#algorithmLink", function() {
    mainContainer.animate({opacity:"0"}, function() {
      mainContainer.load("AlgorithmMainContainer.html", function() {
        mainContainer.animate({opacity:"1"});
      });
    });
  });
  
  $(document).on("click","#codeLink", function() {
    mainContainer.animate({opacity:"0"}, function() {
      mainContainer.load("CodeMainContainer.html", function() {
        mainContainer.animate({opacity:"1"});
      });
    });
  });
  
  $(document).on("mouseenter",".homeLink", function() {
    $(".homeLink").animate({opacity:"0"});
  });
  
  $(document).on("mouseleave",".homeLink", function() {
    $(".homeLink").animate({opacity:"1"});
  });
  
  $(document).on("click",".homeLink", function() {
    mainContainer.animate({opacity:"0"}, function() {
      mainContainer.load("HomeMainContainer.html", function() {
        mainContainer.animate({opacity:"1"});
      });
    });
  });
});