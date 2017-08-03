/**
 * 
 */
var gdadocerapp = angular.module('GDADocerApp', ['ngResource', 'ui.bootstrap', 'ngFileUpload', 'angular-loading-bar', 'toaster', 'ngAnimate'])
/**
 * Service SESSIONE passaggio dati per MODAL
 */
.service('SessionService', ['$log', function($log) {
	$log.debug('SessionService');
	var SessionService = this;
	/* id folder corrente */
	SessionService.folderId = null;
	/* parametro EXTERNAL_ID */
	SessionService.externalId = null;
	/* parametro ACL */
	SessionService.acl = null;
	/* dati per gestire revisione di un file */
	SessionService.versione = false;
	SessionService.document = null;

	SessionService.utente = "";
	SessionService.stamp = "";

	return SessionService;
}])
/**
 * Service per risorse REST DOCER
 */
.service('DocerService', ['$resource', '$log', function($resource, $log) {
	$log.debug('DocerService');
	var docerResource = $resource('./api/docer/documents/:id', {
		id : '@id',
		utente : ""
	}, {
		query : {
			method : 'GET',
			isArray : true
		},
		'profiles' : {
			url: './api/docer/documents/:id/profiles',
			method : 'GET',
			isArray : true
		},
		'childs' : {
			url: './api/docer/documents/:id/childs',
			method : 'GET',
			isArray : true
		},		
		'profile' : {
			url: './api/docer/documents/:id/profile',
			method : 'GET',
			isArray : false
		},
		'versions' : {
			url: './api/docer/documents/:id/versions',
			method : 'GET',
			isArray : true
		},
		'delete' : {
			url: './api/docer/documents/:id/delete',
			method : 'DELETE',
			isArray : false
		},
		'downloadall' : {
			url: './api/docer/documents/downloadall',
			method : 'GET',
			isArray : false,
			params: { externalId : 0 }
		}
	});
	return docerResource;
}])
/**
 * Controller della pagina MAIN
 */
.controller('MainController', ['$log', '$scope', '$location', 'DocerService', '$uibModal', 'SessionService', '$timeout', 'toaster', '$filter', function($log, $scope, $location, docerService, $uibModal, SessionService, $timeout, toaster, $filter) {
	// var $ctrl = this;
	
	// caricamento profilo utente
	// view, delete, download, parent, upload, version
	// default tutto abilitato
	$scope.profile = {
		admin:false, view:true, "delete":true, download:true, parent:true, upload:true, version:true
	};
	if ($location.search().profile || $location.search().flags) {
		 var profile = ($location.search().profile) ? $location.search().profile : $location.search().flags;
		 $log.debug('param profile='+profile);
		 $scope.profile = {
			 admin:(profile[0]==='2'),
			 view:(profile[0]>='1'),"delete":(profile[1]==='1'),download:(profile[2]==='1'),parent:(profile[3]==='1'),upload:(profile[4]==='1'),version:(profile[5]==='1')
		 };
	}
	$log.debug('profile='+angular.toJson($scope.profile));
	
	$scope.folderIdParent = null;
	$scope.folderId = null;
//	if ($location.search().id) {
//		$scope.folderId = $location.search().id;
//		$log.debug('folderId='+$scope.folderId);
//	} else {
//		// mancato parametro id inibisce visualizzazione
//		$scope.profile.view = false;
//	}
	/* EXTERNAL_ID */
	$scope.externalId = null;
	if ($location.search().externalId) {
		$scope.externalId = $location.search().externalId;
		$log.debug('externalId='+$scope.externalId);
	} else {
		// mancato parametro id inibisce visualizzazione
		$scope.profile.view = false;
	}
	/* */
	$scope.acl = null;
	if ($location.search().acl || $location.search().acls) {
		var aclParam = ($location.search().acl) ? decodeURIComponent($location.search().acl) : decodeURIComponent($location.search().acls);
		$log.debug('acl='+aclParam);
		$scope.acl = angular.fromJson(aclParam);
	}
    /* utente per autenticarsi */
	
    SessionService.utente = "";
	if ($location.search().utente) {
        SessionService.utente = $location.search().utente;
        $log.debug('utente='+SessionService.utente);
	}
	$scope.utente = SessionService.utente;
	
	/* stamp data */
    SessionService.stamp = "";
	if ($location.search().stamp) {
        var stamp = decodeURIComponent($location.search().stamp);
        $scope.stamp = angular.fromJson(stamp);
        $log.debug('stamp='+stamp);
        SessionService.stamp = $scope.stamp;
	}

	// elenco dei documenti della cartella
	$scope.docs = [];
	// indica se la cartella ha un documento principale, utile per il popup upload disabilita opzione principale
	$scope.hasPrincipale = false;
	// campo di ordinamento predefinito
	$scope.orderByField='DOCNAME';
	// criterio ordinamento
	$scope.sortAsc=true;
	// variabili per indicare se stiamo caricando dati rest (utile per visualizzare icone loading) non usate
	$scope.isLoadingData = false;
	$scope.isLoadingVersioni = false;
	
	$scope.loadData = function() {
		if (!$scope.profile.view) {
			$log.debug('not profile view');
			return;
		}
		$log.debug('loading data');
		$scope.isLoadingData = true;
		$scope.docs = [];
		/* versione 1 */
		/*
		docerService.query({
			id : folderId
		}, function (folderData) {
			if (folderData) {
				$log.debug('data loaded');
				// $scope.docs = folderData;
				angular.forEach(folderData, function(childDocumenId) {
					docerService.profile({
						id : childDocumenId
					}, function (documentProfile) {
						if (documentProfile) {
							$scope.docs.push(documentProfile);
						}
					});
				});
			} else {
				$log.debug('no data loaded');
				// TODO: messaggio noda
			}
		});
		*/
		if ($scope.folderId) {
			/* versione 2 */
			docerService.childs({
				id : $scope.folderId,
				utente : SessionService.utente
			}, function (childs) {
				$scope.isLoadingData = false;
				$log.debug('childs loaded');
				if (childs) {
					$scope.docs = childs;
					// $scope.docs = $filter('orderBy')(childs, $scope.orderByField, $scope.sortAsc);
					$scope.hasPrincipale = ((_.findIndex(childs, {'TIPO_COMPONENTE': 'PRINCIPALE'})) >= 0);
				}
			}, function (childsErrorResponse) {
				$scope.isLoadingData = false;
				$log.error(childsErrorResponse);
				toaster.pop({
	                type: 'error',
	                title: "Errore durante il caricamento dei dati per la Cartella specificata.",
	                body: childsErrorResponse,
	                showCloseButton: true
	            });				
			});
		} else if ($scope.externalId) {
			/* versione 3 */
			docerService.query({
				externalId : $scope.externalId,
				utente : SessionService.utente
			}, function (documents) {
				$scope.isLoadingData = false;
				$log.debug('documents loaded');
				if (documents) {
					$scope.docs = documents;
					// $scope.docs = $filter('orderBy')(childs, $scope.orderByField, $scope.sortAsc);
					$scope.hasPrincipale = ((_.findIndex(documents, {'TIPO_COMPONENTE': 'PRINCIPALE'})) >= 0);
				}
			}, function (childsErrorResponse) {
				$scope.isLoadingData = false;
				$log.error(childsErrorResponse);
				toaster.pop({
	                type: 'error',
	                title: "Errore durante il caricamento dei dati per la Cartella specificata.",
	                body: childsErrorResponse,
	                showCloseButton: true
	            });				
			});			
		}
	};

	$scope.versions = [];
	$scope.selectedDoc = null;
	$scope.resetSelection = function(doc) {
		$scope.versions = [];
		$scope.selectedDoc = null;
	};
	$scope.resetSelection();
	
	$scope.unselectCurrentDoc = function() {
		if ($scope.selectedDoc) {
			$scope.selectedDoc.selected = false; // equivale a doc.selected = false
			$scope.resetSelection();			
		}
	};
	$scope.selectDoc = function(doc) {
		$log.debug("selectDoc");
		// se stesso file lo deseleziono
		// TODO: se seleziono da file a cartella, da file a file, stesso file
		if ($scope.selectedDoc && $scope.selectedDoc.DOCNUM === doc.DOCNUM)
			return;
		else
			$scope.unselectCurrentDoc();
		
//			// $scope.selectedDoc.selected = false; // equivale a doc.selected = false
//			// $scope.resetSelection();
//		} else {
			// deseleziono vecchio
		

		if (doc) {
			// seleziono corrente
			doc.selected = true;
			$scope.selectedDoc = doc;
			if (doc.DOCNUM) {
				$log.debug('loading version for ' + doc.DOCNUM);
				$scope.isLoadingVersions = true;
				docerService.versions({
					id : doc.DOCNUM,
					utente : SessionService.utente
				}, function(versions) {
					$scope.isLoadingVersions = false;
					$scope.versions = versions;
				}, function(versionsErrorResponse) {
					$log.error(versionsErrorResponse);
					toaster.pop({
		                type: 'error',
		                title: "Errore durante il caricamento delle versioni.",
		                body: versionsErrorResponse,
		                showCloseButton: true
		            });				
					$scope.isLoadingVersions = false;				
				});
			}
		}
		
	};
	$scope.browseDoc = function(doc) {
		$scope.unselectCurrentDoc();
		$log.debug("browseDoc");
		$scope.folderIdParent = $scope.folderId;
		$scope.folderId = doc.FOLDER_ID;
		$scope.loadData();
	};
	$scope.clickRow = function(doc) {
		$log.debug("clickRow");
		//if (doc.FOLDER_ID) {
			//$scope.browseDoc(doc)
		//}
		if (doc.DOCNUM) {
			$scope.selectDoc(doc)
		}
	}
	$scope.doubleClickRow = function(doc) {
		$log.debug("doubleClickRow");
		if (doc.FOLDER_ID) {
			$scope.browseDoc(doc)
		}
	};
	/*
	$scope.download = function(doc) {
		$log.debug('download for ' + doc.nome);
		docerService.download({
			id : doc.id
		}, function(response) {
			
			var data = new Blob([response], { type: doc.contentType + ';charset=utf-8' });
		    FileSaver.saveAs(data, doc.nome);
					    
//			var url = URL.createObjectURL(new Blob([ data ]));
//			var a = document.createElement('a');
//			a.href = url;
//			a.download = doc.nome;
//			a.target = '_blank';
//			a.click();
		});
	}
	*/
	$scope.levelUp = function() {
		$scope.unselectCurrentDoc();
		$log.debug("levelUp");
		if ($scope.folderIdParent) {
			$scope.folderId = $scope.folderIdParent;
			$scope.loadData();
		}
	};
		
	$scope.openUploadModals = function() {
		$log.debug('openUploadModals');
//		$uibModal.open({
//			animation : true,
//			ariaLabelledBy : 'modal-title-bottom',
//			ariaDescribedBy : 'modal-body-bottom',
//			templateUrl : 'partials/uploadDocument.html',
//			// size : 'sm',
//			controller : 'UploadController',
//			controllerAs : '$ctrl',
//		});
		
//		$log.debug('SessionService.folderId='+$scope.folderId);
//		SessionService.folderId = $scope.folderId;
		$log.debug('SessionService.externalId='+$scope.externalId);
		SessionService.externalId = $scope.externalId;
		var aclJson = angular.toJson($scope.acl);
		$log.debug('SessionService.acl='+aclJson);
		SessionService.acl = aclJson;
		
		var modalInstance = $uibModal.open({
			animation : true,
			ariaLabelledBy : 'modal-title',
			ariaDescribedBy : 'modal-body',
			templateUrl : 'partials/uploadDocument.html',
			controller : 'UploadController',
			controllerAs : '$ctrl'
			// size : 'lg',
			// appendTo : parentElem,
//			resolve : {
//				folderId : function() {
//					return $ctrl.items;
//				}
//			}
		});
	
		modalInstance.result.then(function () {
			$log.info('Modal OK at: ' + new Date());
			$scope.loadData();
	    }, function () {
    		$log.info('Modal dismissed at: ' + new Date());
	    });	
	};
	
	if ($scope.folderId || $scope.externalId) {
		// carico i dati riferiti a folderId o externalId
		$scope.loadData();
	} else {
		$timeout(function() {
			toaster.pop('warning', "Errore riferimento dati da visualizzare.", "");
		}, 250);
	}
	
	$scope.pop = function(){
        toaster.pop('warning', "title", "text");
    };
    /*
     * pulsante Carica Documento
     */
    $scope.addFile = function() {
    	SessionService.versione = false;
    	SessionService.document = null;
    	SessionService.hasPrincipale = $scope.hasPrincipale;
        $scope.openUploadModals();
    };
    /*
     * pulsante Carica una nuova versione
     */
    $scope.versione = function() {
    	SessionService.versione = true;
    	SessionService.document = $scope.selectedDoc;
        $scope.openUploadModals();
    };
    /*
     * pulsante Elimina Documento
     */
	$scope.deleteFile = function() {
		$log.debug("deleteFile");
		var doc = $scope.selectedDoc;
		if (doc) {
			if (confirm('Sicuro di voler eliminare il file ' + doc.DOCNAME + ' ?')) {
				docerService.delete({
					id : doc.DOCNUM,
					utente : SessionService.utente
				}, function (deleteResponse) {
					$log.debug("deleteResponse=" + deleteResponse);
					if (deleteResponse) {
						$log.debug('doc ' + doc.DOCNUM + ' deleted');
						toaster.pop({
		                    type: 'success',
		                    title: 'File ' + doc.DOCNAME + ' eliminato.',
		                    body: '',
		                    showCloseButton: true
		                });
						$scope.loadData();
					} else {
						toaster.pop({
		                    type: 'warning',
		                    title: 'File ' + doc.DOCNAME + ' eliminato.',
		                    body: '',
		                    showCloseButton: true
		                });
					}
				}, function (deleteError) {
					toaster.pop({
	                    type: 'error',
	                    title: "Errore durante l'eliminazione del file.",
	                    body: error,
	                    showCloseButton: true
	                });
				});
				/*
				var resource = docerService.get({
					id : doc.DOCNUM
				}, function() {
					$log.debug(angular.toJson(doc));
					// user.abc = true;
					// user.$save();
					resource.$delete();
				});
				*/
			}
		}
	};
}])
/**
 * Controller per POPUP UPLOAD
 */
.controller('UploadController', ['$log', '$scope', '$uibModalInstance', 'Upload', 'SessionService', 'toaster', function($log, $scope, $uibModalInstance, Upload, SessionService, toaster) {
	var $ctrl = this;
	// $ctrl.folderId = SessionService.folderId;
	// $scope.folderId = SessionService.folderId;
//	$ctrl.selected = {
//		item : $ctrl.items[0]
//	};
	// $ctrl.folderId = $ctrl.folderId;
	$ctrl.titoloPopup = "Documento";
	$ctrl.revisione = false;
	if (SessionService.versione) {
		$ctrl.titoloPopup = "Versione";
		$ctrl.revisione = true;
	}
	// verifico se la cartella ha già un principale
	$ctrl.hasPrincipale = SessionService.hasPrincipale;
	// pre-selezione combo tipoComponente su voce adeguata
	if ($ctrl.hasPrincipale) {
		$scope.tipoComponente = 'ALLEGATO';
	} else {
		$scope.tipoComponente = 'PRINCIPALE';
	}
	
	$ctrl.ok = function() {
		$scope.submit();
		// $uibModalInstance.close();
	};

	$ctrl.cancel = function() {
		$uibModalInstance.dismiss('cancel');
	};

	// upload later on form submit or something similar
    $scope.submit = function() {
		if ($scope.form.file.$valid && $scope.file) {
			$scope.upload($scope.file);
		}
	};

	$scope.uploading = false;
	$scope.progressPercentage = 0;
	// upload on file select or drop
    $scope.upload = function (file) {
    	var uploadUrl = '';
    	var uploadData = {};
    	if (SessionService.versione) {
    		$log.debug("revisione documento " + SessionService.document.DOCNUM);
    		uploadUrl = './api/docer/documents/'+SessionService.document.DOCNUM+'/versione';
    		uploadData = {
				abstract : $scope.abstract,
				utente: SessionService.utente,
				file : file
			};
    	} else {
    		if (SessionService.folderId) {
	    		$log.debug("upload su folder " + SessionService.folderId);
	    		uploadUrl = './api/docer/documents/'+SessionService.folderId+'/upload';
	    		uploadData = {
					abstract : $scope.abstract,
	    			tipoComponente : $scope.tipoComponente,
	    			acl : SessionService.acl,
	    			utente: SessionService.utente,
	    			file : file
	    		};
    		} else {
    			$log.debug("upload su docer " + SessionService.externalId);
	    		uploadUrl = './api/docer/documents/upload';
	    		uploadData = {
    				externalId : SessionService.externalId,
					abstract : $scope.abstract,
	    			tipoComponente : $scope.tipoComponente,
	    			acl : SessionService.acl,
	    			utente: SessionService.utente,
	    			file : file, 
	    		};
    		}
    	}
    	// chiamata a metodo upload con parametri url e data impostati in precedenza
		Upload.upload({
            url : uploadUrl,
			data : uploadData
        }).then(function (resp) {
        	// $scope.uploading = false;
        	$log.debug('Success ' + resp.config.data.file.name + 'uploaded. Response: ' + resp.data);
        	toaster.pop({
                type: 'success',
                title: 'File caricato',
                body: '',
                showCloseButton: true
            });
            $uibModalInstance.close();
        }, function (resp) {
        	// $log.error('Error status: ' + resp.status);
        	$log.error(resp);
        	toaster.pop({
                type: 'error',
                title: 'Errore caricamento file',
                body: 'Errore: ' + resp.status + ' - ' + resp.data,
                showCloseButton: true
            });
        }, function (evt) {
        	$scope.uploading = true;
            $scope.progressPercentage = parseInt(100.0 * evt.loaded / evt.total);
            $log.debug('progress: ' + $scope.progressPercentage + '% ' + evt.config.data.file.name);
        });    		
    };
}]);