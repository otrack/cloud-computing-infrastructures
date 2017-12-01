import web
import delegator
import uuid
import socket

urls = (
    '/', 'Upload',
    '/Download/(.+)', 'Download'
    )    
    
filesdir = '/tmp'
host = str(socket.gethostbyname(socket.gethostname()))
    
class Download:

    def GET(self,id):
        path=filesdir +'/'+str(id)+'.pdf'
        web.header('Content-Disposition', 'attachment; filename="'+str(id)+'.pdf"')
        web.header('Content-type','application/pdf')
        web.header('Content-transfer-encoding','binary') 
        return open(path, 'rb').read()

class Upload:
        
    def GET(self):        
        web.header("Content-Type","text/html; charset=utf-8")        
        return """<html><head></head><body>welcome @"""+host+"""; please upload your file:
<form method="POST" enctype="multipart/form-data" action="">
<input type="file" name="myfile" />
<br/>
<input type="submit" />
</form>
</body></html>"""

    def POST(self):
        x = web.input(myfile={})

        if 'myfile' in x:            
            id=str(uuid.uuid4()) # random uuid
            path=filesdir +'/'+ str(id)
            fout = open(path+'.jpg','w')
            fout.write(x.myfile.file.read())
            fout.close()
            delegator.run('convert '+path+'.jpg '+path+'.pdf')
            raise web.seeother('/Download/'+id)

if __name__ == "__main__":
   app = web.application(urls, globals()) 
   app.run()
