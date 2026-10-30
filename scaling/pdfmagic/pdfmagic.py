import web
import delegator
import uuid
import socket
import os

urls = (
    '/', 'Upload',
    )    
    
filesdir = '/tmp'
host = str(socket.gethostbyname(socket.gethostname()))
    
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
            try:
                fout = open(path+'.jpg','wb')
                fout.write(x.myfile.file.read())
                fout.close()
                delegator.run('convert '+path+'.jpg '+path+'.pdf')
                pdf = open(path+'.pdf', 'rb').read()
            finally: # the files live only during the request
                for f in (path+'.jpg', path+'.pdf'):
                    if os.path.exists(f):
                        os.remove(f)
            web.header('Content-Disposition', 'attachment; filename="'+id+'.pdf"')
            web.header('Content-type','application/pdf')
            return pdf

if __name__ == "__main__":
   app = web.application(urls, globals()) 
   app.run()
