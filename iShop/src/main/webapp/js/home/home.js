$(function(){
	var val=sessionStorage.getItem("key");
    val=JSON.parse(val);
    var message=JSON.parse(val.message);
    console.log(val);
    console.log(message);
    var index=0;
    for(index in message.menu){
        console.log(message.menu[index]);
        var html +='<li>'
                 +'<a href="index.html">'
                     +'<i class="icon-ishop_2-01"></i>'
                     +'<span class="nav-label">'+message.menu[index].mod_name+'</span> <span class="fa arrow"></span>'
                 +'</a>'
                 // +'<ul class="nav nav-second-level">'
                 //     +'<li><a href="#">示例一</a>'
                 //     +'</li>'
                 //     +'<li><a href="#">示例二</a>'
                 //     +'</li>'
                 //     +'<li><a href="#">示例三</a>'
                 //     +'</li>'
                 //     +'<li><a href="#">示例四</a>'
                 //     +'</li>'
                 // +'</ul>'
             +'</li>';
            //  +'<li>'
            //      +'<a href="#"><i class="icon-ishop_2-02"></i> <span class="nav-label">'+message.menu[1].mod_name+'</span>'
            //      +'<span class="fa arrow"></span>'
            //      +'</a>'
            //      +'<ul class="nav nav-second-level">'
            //         +'<li><a href="#">'+message.menu[1].functions[0].fun_name+'</a>'
            //         +'</li>'
            //      +'</ul>'
            //  +'</li>'
            // //  +'<li>'
            // //      +'<a href="#"><i class="icon-ishop_2-03"></i> <span class="nav-label">'+message.menu[2].mod_name+'</span><span class="fa arrow"></span></a>'
            // //      +'<ul class="nav nav-second-level">'
            // //          +'<li><a href="#">'+message.menu[2].functions[0].fun_name+'</a>'
            // //          +'</li>'
            // //          +'<li><a href="#">'+message.menu[2].functions[1].fun_name+'</a>'
            // //          +'</li>'
            // //      +'</ul>'
            // //  +'</li>'
            // // +'<li>'
            // //     +'<a href="#"><i class="icon-ishop_2-04"></i> <span class="nav-label">'+message.menu[3].mod_name+'</span><span class="fa arrow"></span></a>'
            // //     +'<ul class="nav nav-second-level">'
            // //         +'<li><a href="#">'+message.menu[3].functions[0].fun_name+'</a>'
            // //         +'</li>'
            // //         +'<li><a href="#">'+message.menu[3].functions[1].fun_name+'</a>'
            // //         +'</li>'
            //     +'</ul>'
            // +'</li>';
        if(message.menu[index].functions!=''){

        }
    }
    $(html).insertAfter('.navbar-static-side .sidebar-collapse #side-menu .nav-header');
});