function effettuaQuery(nomeMetodo, successCallback){

    $.ajax({
            type: "POST",
            crossDomain: true,
            crossOrigin: true,
            url: "/" + nomeMetodo,
            dataType : 'json',
            contentType: "application/json; charset=utf-8",
            success: function(data){
                successCallback(data);
            },
            error: function (xhr, status, errorThrown) {
                alert(status, errorThrown);
                // Error block
                console.log("xhr: " + xhr);
                console.log("status: " + status);
                console.log("errorThrown: " + errorThrown);
            }

    });
}




function effettuaQueryAutocomplete(nomeMetodo, nomeInput){

   effettuaQuery(nomeMetodo, function(resultData){

      var items = [];

      for(var i = 0; i < resultData.length; i++){

          var itemData = resultData[i];

          items.push({value:itemData[0], label:itemData[1]});
      }

      $( "#" + nomeInput).autocomplete({
        source: items,
        minLength: 2,
         select: function(event, ui) {
               event.preventDefault();

               $( "#" + nomeInput).val(ui.item.label);
               $( "#" + nomeInput).val(ui.item.label);
               $( "#" + nomeInput).data("value", ui.item.value);
           },
           focus: function(event, ui) {
               event.preventDefault();

               $( "#" + nomeInput).val(ui.item.label);
           }
      });
  });
}