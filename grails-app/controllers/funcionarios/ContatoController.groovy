package funcionarios


import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

@Transactional(readOnly = true)
class ContatoController {

    def beforeInterceptor = [action:this.&auth, except:["index", "list", "show"]]

    def auth() {
      if(session.user && session.user.login != "admin") {
        flash.message = "Desculpe, você não tem permissão para realizar esta ação. =("
        redirect(controller:"funcionario", action:"index")
        return false
      }
    }    

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond Contato.list(params), model:[contatoInstanceCount: Contato.count()]
    }

    def show(Contato contatoInstance) {
        respond contatoInstance
    }

    def create() {
        respond new Contato(params)
    }

    @Transactional
    def save(Contato contatoInstance) {
        if (contatoInstance == null) {
            notFound()
            return
        }

        if (contatoInstance.hasErrors()) {
            respond contatoInstance.errors, view:'create'
            return
        }

        contatoInstance.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'contato.label', default: 'Contato'), contatoInstance.id])
                redirect contatoInstance
            }
            '*' { respond contatoInstance, [status: CREATED] }
        }
    }

    def edit(Contato contatoInstance) {
        respond contatoInstance
    }

    @Transactional
    def update(Contato contatoInstance) {
        if (contatoInstance == null) {
            notFound()
            return
        }

        if (contatoInstance.hasErrors()) {
            respond contatoInstance.errors, view:'edit'
            return
        }

        contatoInstance.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'Contato.label', default: 'Contato'), contatoInstance.id])
                redirect contatoInstance
            }
            '*'{ respond contatoInstance, [status: OK] }
        }
    }

    @Transactional
    def delete(Contato contatoInstance) {

        if (contatoInstance == null) {
            notFound()
            return
        }

        contatoInstance.delete flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'Contato.label', default: 'Contato'), contatoInstance.id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'contato.label', default: 'Contato'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
