---
layout: page
title: Cheeze Factory
comments: no
permalink: /factory
---
Start your devfiles on your favorite Che or CodeReady workpaces:

Default server: https://che.openshift.io/

<script>
            var selectedServer = "https://che.openshift.io/";
            var allcookies = document.cookie;
            console.log(allcookies);
            // Get all the cookies pairs in an array
            cookiearray = allcookies.split('; ');
            var selected;

            for(var i=0; i<cookiearray.length; i++) {
               name = cookiearray[i].split('=')[0];
               value = cookiearray[i].split('=')[1];
               if(name == "server"){
                  selected=value.substring(4);
               }
            }
            var servers = getServers(cookiearray);

            if(servers.length > 0){
                if(!selected){
                    selected = servers[0].name;
                }

                document.write("<h2>Select the server to use:</h2>");

                // Now take key value pair out of this array
                document.write("<select id='server' onchange=\"document.location.reload(setServer(document.getElementById('server').value))\">");

                for(var i=0; i<servers.length; i++){
                    var server = servers[i];
                    console.log(getServers(cookiearray));
                    document.write ("  <option value='"+server.fullname+"' "+setSelected(server.name)+">"+unescape(server.name) + ": " + unescape(server.url)+"</option>");
                    if(isSelected(server.name)){
                        selectedServer = server.url;
                    }
                }
                document.write("</select>");
            }
            if(selected){
                document.write("<br/>Selected server: <a href='"+unescape(selectedServer)+"'>"+ unescape(selected)+"</a><br/>")
                console.log('selected server: '+unescape(selectedServer));
            }
            var urlParams = new URLSearchParams(window.location.search);
            if(urlParams.has('url')){
                setDevfileHistory(urlParams.get('url'));
                window.location.href = unescape(selectedServer) + "f?url="+urlParams.get('url');
            }


            var today = new Date();
            var expiry = new Date(today.getTime() + 36 * 30 * 24 * 3600 * 1000); // plus 36 * 30 days

            function setSelected(value){
                if(isSelected(value)){
                    return "selected";
                }
                return "";
            }
            function isSelected(value){
                console.log('isSelected: value ' + value );
                if(!value){
                    return false;
                }
                console.log(value + ' '+ selected)
                if(value==selected){
                    return true;
                }
                return false;
            }

            function setDevfileHistory(url) {
            {
                var today = new Date();
                var expiry = new Date(today.getTime() + 36 * 30 * 24 * 3600 * 1000); // plus 36 * 30 days
                document.cookie='devfile_'+ escape(url) + "=" + escape(url) + "; path=/; expires=" + expiry.toGMTString();
            }
            }

            function setCookie(name, url)
            {
              if(!url.endsWith('/')){
                  url = url + '/';
              }

              if(!url.startsWith('http://') && !url.startsWith('https://') ){
                  url = "https://" + url;
              }

              document.cookie='che_'+ escape(name) + "=" + escape(url) + "; path=/; expires=" + expiry.toGMTString();
            }

            function setServer(value)
            {
              document.cookie="server=" + escape(value) + "; path=/; expires=" + expiry.toGMTString();
              return true;
            }

            function putCookie(form)
            {
              setCookie(form[0].name.value, form[0].url.value);
              return true;
            }

            function getServers(){
               var servers = [];
               for(var i=0; i<cookiearray.length; i++) {
                   name = cookiearray[i].split('=')[0];
                   value = cookiearray[i].split('=')[1];
                   if(name.startsWith("che_")){
                       servers.push({
                                        "fullname" : name,
                                        "name" : name.substring(4),
                                        "url" : value
                                    });
                   }
                }
                return servers;
            }

            function getDevfileHistory(){
                var devfiles = [];
                for(var i=0; i<cookiearray.length; i++) {
                    name = cookiearray[i].split('=')[0];
                    value = cookiearray[i].split('=')[1];
                    if(name.startsWith("devfile_")){
                       devfiles.push(value);
                    }
                }
                return devfiles;
            }

</script>

<form>
    <h2>Add a new Che/CodeReady Workspaces server</h2>
    Name: <input type="text" id="name" name='name'><br />
    URL: <input type="text" id="url" name='url'><br />
    <input type="button" value="Add/update" id="submit" onclick="document.location.reload(putCookie(document.getElementsByTagName('form')));">
</form>

<script>
    if(!selected){
        selected = "My CheCRW server"
    }
    document.getElementsByTagName('form')[0].name.value = unescape(selected);
    document.getElementsByTagName('form')[0].url.value = unescape(selectedServer);
</script>

## Update your badge
```
<a href="https://blog.sunix.org/factory?url=https://github.com/sunix/blog.sunix.org/tree/gh-pages"><img src="https://www.eclipse.org/che/contribute.svg" /></a>
```

Like that: <a href="https://blog.sunix.org/factory?url=https://github.com/sunix/blog.sunix.org/tree/gh-pages"><img src="https://www.eclipse.org/che/contribute.svg" /></a>

## Previous devfile:
<ol>
<script>
    var devfiles = getDevfileHistory();
    for(var i=0; i<devfiles.length; i++) {
        devfile = devfiles[i];
        document.write("<li><a href='"+ window.location.href + "?url=" + devfile + "' >"+ unescape(devfile) + "</li>");
    }
</script>
</ol>
