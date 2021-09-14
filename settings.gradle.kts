rootProject.name = "interaction-fields"

include("if-auth")
include("if-common")
include("if-gateway")
include("if-rpc")
include("if-service")

include("if-service:if-service-auth")
findProject(":if-service:if-service-auth")?.name = "if-service-auth"

include("if-service:if-service-meeting")
findProject(":if-service:if-service-meeting")?.name = "if-service-meeting"

include("if-service:if-service-user")
findProject(":if-service:if-service-user")?.name = "if-service-user"

include("if-service:if-service-signaling")
findProject(":if-service:if-service-signaling")?.name = "if-service-signaling"

include("if-service:if-service-judge")
findProject(":if-service:if-service-judge")?.name = "if-service-judge"
