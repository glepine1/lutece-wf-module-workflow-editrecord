/*
 * Copyright (c) 2002-2011, Mairie de Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.workflow.modules.editrecord.service;

import fr.paris.lutece.plugins.blobstoreclient.service.BlobStoreClientWebService;
import fr.paris.lutece.plugins.directory.business.EntryFilter;
import fr.paris.lutece.plugins.directory.business.EntryHome;
import fr.paris.lutece.plugins.directory.business.EntryType;
import fr.paris.lutece.plugins.directory.business.EntryTypeDownloadUrl;
import fr.paris.lutece.plugins.directory.business.EntryTypeHome;
import fr.paris.lutece.plugins.directory.business.Field;
import fr.paris.lutece.plugins.directory.business.IEntry;
import fr.paris.lutece.plugins.directory.business.PhysicalFile;
import fr.paris.lutece.plugins.directory.business.PhysicalFileHome;
import fr.paris.lutece.plugins.directory.business.Record;
import fr.paris.lutece.plugins.directory.business.RecordField;
import fr.paris.lutece.plugins.directory.business.RecordFieldFilter;
import fr.paris.lutece.plugins.directory.business.RecordFieldHome;
import fr.paris.lutece.plugins.directory.business.RecordHome;
import fr.paris.lutece.plugins.directory.service.DirectoryPlugin;
import fr.paris.lutece.plugins.directory.utils.DirectoryErrorException;
import fr.paris.lutece.plugins.directory.utils.DirectoryUtils;
import fr.paris.lutece.plugins.workflow.business.ActionHome;
import fr.paris.lutece.plugins.workflow.business.ResourceHistory;
import fr.paris.lutece.plugins.workflow.business.ResourceHistoryHome;
import fr.paris.lutece.plugins.workflow.business.ResourceWorkflow;
import fr.paris.lutece.plugins.workflow.business.ResourceWorkflowHome;
import fr.paris.lutece.plugins.workflow.business.StateFilter;
import fr.paris.lutece.plugins.workflow.business.StateHome;
import fr.paris.lutece.plugins.workflow.business.task.ITask;
import fr.paris.lutece.plugins.workflow.business.task.TaskHome;
import fr.paris.lutece.plugins.workflow.modules.editrecord.business.EditRecord;
import fr.paris.lutece.plugins.workflow.modules.editrecord.business.EditRecordHome;
import fr.paris.lutece.plugins.workflow.modules.editrecord.business.EditRecordValue;
import fr.paris.lutece.plugins.workflow.modules.editrecord.business.TaskEditRecordConfig;
import fr.paris.lutece.plugins.workflow.modules.editrecord.service.signrequest.EditRecordRequestAuthenticatorService;
import fr.paris.lutece.plugins.workflow.modules.editrecord.util.UrlUtils;
import fr.paris.lutece.plugins.workflow.modules.editrecord.util.constants.EditRecordConstants;
import fr.paris.lutece.plugins.workflow.service.WorkflowPlugin;
import fr.paris.lutece.plugins.workflow.service.WorkflowService;
import fr.paris.lutece.plugins.workflow.utils.WorkflowUtils;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.business.workflow.Action;
import fr.paris.lutece.portal.business.workflow.State;
import fr.paris.lutece.portal.service.admin.AdminUserService;
import fr.paris.lutece.portal.service.message.SiteMessage;
import fr.paris.lutece.portal.service.message.SiteMessageException;
import fr.paris.lutece.portal.service.message.SiteMessageService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.httpaccess.HttpAccessException;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;


/**
 *
 * EditRecordService
 *
 */
public final class EditRecordService
{
    private static final String BEAN_EDIT_RECORD_SERVICE = "workflow-editrecord.editRecordService";
    private EditRecordValueService _editRecordValueService;
    private BlobStoreClientWebService _blobStoreClientWS;

    /**
     * Private constructor
     */
    private EditRecordService(  )
    {
    }

    /**
     * Get the instance of the service
     * @return the instance of the service
     */
    public static EditRecordService getService(  )
    {
        return (EditRecordService) SpringContextService.getPluginBean( EditRecordPlugin.PLUGIN_NAME,
            BEAN_EDIT_RECORD_SERVICE );
    }

    // SET

    /**
     * Set the site message
     * @param request the HTTP request
     * @param strMessage the message
     * @param nTypeMessage the message type
     * @param strUrlReturn the url return
     * @throws SiteMessageException the site message
     */
    public void setSiteMessage( HttpServletRequest request, String strMessage, int nTypeMessage, String strUrlReturn )
        throws SiteMessageException
    {
        if ( StringUtils.isNotBlank( strUrlReturn ) )
        {
            SiteMessageService.setMessage( request, strMessage, nTypeMessage, strUrlReturn );
        }
        else
        {
            SiteMessageService.setMessage( request, strMessage, nTypeMessage );
        }
    }

    /**
     * Set the edit record value service
     * @param editRecordValueService the edit record value service
     */
    public void setEditRecordValueService( EditRecordValueService editRecordValueService )
    {
        _editRecordValueService = editRecordValueService;
    }

    /**
     * Set the blobstore client web service
     * @param blobStoreClientWebService the blob store client web service
     */
    public void setBlobStoreClientWebService( BlobStoreClientWebService blobStoreClientWebService )
    {
        _blobStoreClientWS = blobStoreClientWebService;
    }

    // CRUD

    /**
     * Create an edit record
     * @param editRecord the edit record
     */
    public void create( EditRecord editRecord )
    {
        if ( editRecord != null )
        {
            EditRecordHome.create( editRecord );

            for ( EditRecordValue editRecordValue : editRecord.getListEditRecordValues(  ) )
            {
                editRecordValue.setIdHistory( editRecord.getIdHistory(  ) );
                _editRecordValueService.create( editRecordValue );
            }
        }
    }

    /**
     * Update an edit record
     * @param editRecord the edit record
     */
    public void update( EditRecord editRecord )
    {
        if ( editRecord != null )
        {
            EditRecordHome.update( editRecord );
            // Remove its edit record values first
            _editRecordValueService.remove( editRecord.getIdHistory(  ) );

            for ( EditRecordValue editRecordValue : editRecord.getListEditRecordValues(  ) )
            {
                editRecordValue.setIdHistory( editRecord.getIdHistory(  ) );
                _editRecordValueService.create( editRecordValue );
            }
        }
    }

    /**
     * Find an edit record
     * @param nIdHistory the id history
     * @param nIdTask the id task
     * @return a edit record
     */
    public EditRecord find( int nIdHistory, int nIdTask )
    {
        EditRecord editRecord = EditRecordHome.find( nIdHistory, nIdTask );

        if ( editRecord != null )
        {
            editRecord.setListEditRecordValues( _editRecordValueService.find( editRecord.getIdHistory(  ) ) );
        }

        return editRecord;
    }

    /**
     * Find edit records by a given id task
     * @param nIdTask the id task
     * @return the list of edit records
     */
    public List<EditRecord> findByIdTask( int nIdTask )
    {
        return EditRecordHome.findByIdTask( nIdTask );
    }

    /**
     * Remove an edit record
     * @param nIdHistory the id history
     * @param nIdTask the id task
     */
    public void removeByIdHistory( int nIdHistory, int nIdTask )
    {
        EditRecord editRecord = find( nIdHistory, nIdTask );
        _editRecordValueService.remove( editRecord.getIdHistory(  ) );
        EditRecordHome.removeByIdHistory( nIdHistory, nIdTask );
    }

    /**
     * Remove an edit record by id task
     * @param nIdTask the id task
     */
    public void removeByIdTask( int nIdTask )
    {
        for ( EditRecord editRecord : findByIdTask( nIdTask ) )
        {
            _editRecordValueService.remove( editRecord.getIdHistory(  ) );
        }

        EditRecordHome.removeByIdTask( nIdTask );
    }

    // GET

    /**
     * Get the list of states
     * @param nIdAction the id action
     * @return a ReferenceList
     */
    public ReferenceList getListStates( int nIdAction )
    {
        Plugin pluginWorkflow = PluginService.getPlugin( WorkflowPlugin.PLUGIN_NAME );
        ReferenceList referenceListStates = new ReferenceList(  );
        Action action = ActionHome.findByPrimaryKey( nIdAction, pluginWorkflow );

        if ( ( action != null ) && ( action.getWorkflow(  ) != null ) )
        {
            StateFilter stateFilter = new StateFilter(  );
            stateFilter.setIdWorkflow( action.getWorkflow(  ).getId(  ) );

            List<State> listStates = StateHome.getListStateByFilter( stateFilter, pluginWorkflow );

            referenceListStates.addItem( DirectoryUtils.CONSTANT_ID_NULL, StringUtils.EMPTY );
            referenceListStates.addAll( ReferenceList.convert( listStates, EditRecordConstants.ID,
                    EditRecordConstants.NAME, true ) );
        }

        return referenceListStates;
    }

    /**
     * Get the list of entries for the form
     * @param nIdRecord the id record
     * @param nIdTask the id task
     * @param request the HTTP request
     * @return a list of entries
     */
    public List<IEntry> getFormListEntries( int nIdRecord, int nIdTask, HttpServletRequest request )
    {
        AdminUser user = AdminUserService.getAdminUser( request );
        Plugin pluginDirectory = PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME );
        List<IEntry> listEntries = new ArrayList<IEntry>(  );

        Record record = RecordHome.findByPrimaryKey( nIdRecord, pluginDirectory );

        if ( record != null )
        {
            listEntries = DirectoryUtils.getFormEntries( record.getDirectory(  ).getIdDirectory(  ), pluginDirectory,
                    user );
        }

        return listEntries;
    }

    /**
     * Get the list of entries for information
     * @param nIdHistory the id edit record
     * @return a list of entries
     */
    public List<IEntry> getInformationListEntries( int nIdHistory )
    {
        List<EditRecordValue> listEditRecordValues = _editRecordValueService.find( nIdHistory );
        List<IEntry> listEntries = new ArrayList<IEntry>(  );
        Plugin pluginDirectory = PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME );

        for ( EditRecordValue editRecordValue : listEditRecordValues )
        {
            IEntry entry = EntryHome.findByPrimaryKey( editRecordValue.getIdEntry(  ), pluginDirectory );

            if ( entry != null )
            {
                listEntries.add( entry );
            }
        }

        return listEntries;
    }

    /**
     * Get the list of entries to edit
     * @param request the HTTP request
     * @param listEditRecordValues the list of edit record values
     * @return a list of entries
     */
    public List<IEntry> getListEntriesToEdit( HttpServletRequest request, List<EditRecordValue> listEditRecordValues )
    {
        Plugin pluginDirectory = PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME );
        List<IEntry> listEntries = new ArrayList<IEntry>(  );

        for ( EditRecordValue editRecordValue : listEditRecordValues )
        {
            IEntry entry = EntryHome.findByPrimaryKey( editRecordValue.getIdEntry(  ), pluginDirectory );

            if ( entry.isRoleAssociated(  ) )
            {
                entry.setFields( DirectoryUtils.getAuthorizedFieldsByRole( request, entry.getFields(  ) ) );
            }

            listEntries.add( entry );
        }

        return listEntries;
    }

    /**
     * Get the list of entries to not edit
     * @param request the HTTP request
     * @param nIdRecord the id record
     * @param listEditRecordValues the list of edit record values
     * @return a list of entries
     */
    public List<IEntry> getListEntriesToNotEdit( HttpServletRequest request, int nIdRecord,
        List<EditRecordValue> listEditRecordValues )
    {
        Plugin pluginDirectory = PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME );
        List<IEntry> listEntriesToNotEdit = new ArrayList<IEntry>(  );
        Record record = RecordHome.findByPrimaryKey( nIdRecord, pluginDirectory );

        if ( record != null )
        {
            // List all entries of the directory
            EntryFilter eFilter = new EntryFilter(  );
            eFilter.setIdDirectory( record.getDirectory(  ).getIdDirectory(  ) );

            List<IEntry> listEntries = EntryHome.getEntryList( eFilter, pluginDirectory );

            // List entries to edit
            List<IEntry> listEntriesToEdit = getListEntriesToEdit( request, listEditRecordValues );

            // Extract the entries to not edit from the list of all entries
            for ( IEntry entry : listEntries )
            {
                boolean bIsEntryToEdit = false;

                for ( IEntry entryToEdit : listEntriesToEdit )
                {
                    if ( entry.getIdEntry(  ) == entryToEdit.getIdEntry(  ) )
                    {
                        bIsEntryToEdit = true;

                        break;
                    }
                }

                if ( !bIsEntryToEdit )
                {
                    listEntriesToNotEdit.add( entry );
                }
            }
        }

        return listEntriesToNotEdit;
    }

    /**
     * Get the list of record fieds to not edit
     * @param request the HTTP request
     * @param nIdRecord the id record
     * @param listEditRecordValues the list of edit record values
     * @return a list of record fields
     */
    public List<RecordField> getListRecordFieldsToNotEdit( HttpServletRequest request, int nIdRecord,
        List<EditRecordValue> listEditRecordValues )
    {
        Plugin pluginDirectory = PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME );

        // List record fields to not edit
        List<RecordField> listRecordFieldsToNotEdit = new ArrayList<RecordField>(  );

        // List all record fields of the record
        RecordFieldFilter filter = new RecordFieldFilter(  );
        filter.setIdRecord( nIdRecord );

        List<RecordField> listRecordField = RecordFieldHome.getRecordFieldList( filter, pluginDirectory );

        // List entries to not edit
        List<IEntry> listEntriesToNotEdit = getListEntriesToNotEdit( request, nIdRecord, listEditRecordValues );

        // Extract the record field to not edit from the list of all record fields
        for ( RecordField recordField : listRecordField )
        {
            for ( IEntry entry : listEntriesToNotEdit )
            {
                if ( recordField.getEntry(  ).getIdEntry(  ) == entry.getIdEntry(  ) )
                {
                    if ( recordField.getFile(  ) != null )
                    {
                        PhysicalFile physicalFile = PhysicalFileHome.findByPrimaryKey( recordField.getFile(  )
                                                                                                  .getPhysicalFile(  )
                                                                                                  .getIdPhysicalFile(  ),
                                pluginDirectory );
                        recordField.getFile(  ).setPhysicalFile( physicalFile );
                    }

                    listRecordFieldsToNotEdit.add( recordField );
                }
            }
        }

        return listRecordFieldsToNotEdit;
    }

    /**
     * Get the map id entry - list record fields
     * @param listEntries the list of entries to edit
     * @param nIdHistory the id history
     * @return a map of id entry - list record fields
     */
    public Map<String, List<RecordField>> getMapIdEntryListRecordField( List<IEntry> listEntries, int nIdHistory )
    {
        Plugin pluginDirectory = PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME );
        Record record = getRecordFromIdHistory( nIdHistory );

        return DirectoryUtils.getMapIdEntryListRecordField( listEntries, record.getIdRecord(  ), pluginDirectory );
    }

    /**
     * Get the entry from a given id entry
     * @param nIdEntry the id entry
     * @return an {@link IEntry}
     */
    public IEntry getEntry( int nIdEntry )
    {
        Plugin pluginDirectory = PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME );

        return EntryHome.findByPrimaryKey( nIdEntry, pluginDirectory );
    }

    /**
     * Get the entry type download url
     * @return the entry type downlaod url
     */
    public EntryType getEntryTypeDownloadUrl(  )
    {
        EntryType entryTypeDownloadUrl = null;
        Plugin pluginDirectory = PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME );

        for ( EntryType entryType : EntryTypeHome.getList( pluginDirectory ) )
        {
            if ( EntryTypeDownloadUrl.class.getName(  ).equals( entryType.getClassName(  ) ) )
            {
                entryTypeDownloadUrl = entryType;

                break;
            }
        }

        return entryTypeDownloadUrl;
    }

    /**
     * Get the record from a given id history
     * @param nIdHistory the id history
     * @return the record
     */
    public Record getRecordFromIdHistory( int nIdHistory )
    {
        Record record = null;
        Plugin pluginWorkflow = PluginService.getPlugin( WorkflowPlugin.PLUGIN_NAME );
        ResourceHistory resourceHistory = ResourceHistoryHome.findByPrimaryKey( nIdHistory, pluginWorkflow );

        if ( ( resourceHistory != null ) &&
                Record.WORKFLOW_RESOURCE_TYPE.equals( resourceHistory.getResourceType(  ) ) )
        {
            Plugin pluginDirectory = PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME );

            // Record
            record = RecordHome.findByPrimaryKey( resourceHistory.getIdResource(  ), pluginDirectory );
        }

        return record;
    }

    /**
     * Get the list of record fields from a given id history and id entry
     * @param nIdHistory the id history
     * @param nIdEntry the id entry
     * @return the list of record fields
     */
    public List<RecordField> getRecordFieldsList( int nIdHistory, int nIdEntry )
    {
        List<RecordField> listRecordFields = null;
        Record record = getRecordFromIdHistory( nIdHistory );

        if ( record != null )
        {
            Plugin pluginDirectory = PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME );
            RecordFieldFilter recordFieldFilter = new RecordFieldFilter(  );
            recordFieldFilter.setIdDirectory( record.getDirectory(  ).getIdDirectory(  ) );
            recordFieldFilter.setIdEntry( nIdEntry );
            recordFieldFilter.setIdRecord( record.getIdRecord(  ) );

            listRecordFields = RecordFieldHome.getRecordFieldList( recordFieldFilter, pluginDirectory );
        }

        return listRecordFields;
    }

    /**
     * Get the record field associated to the entry type download url.
     * <br />
     * There is currently only on record field per record for the
     * entry type download url. So, this method will only fetch the
     * first record field.
     * @param nIdHistory the id history
     * @param nIdEntry the id entry
     * @return the record field
     */
    public RecordField getRecordFieldDownloadUrl( int nIdHistory, int nIdEntry )
    {
        RecordField recordField = null;
        List<RecordField> listRecordField = getRecordFieldsList( nIdHistory, nIdEntry );

        if ( ( listRecordField != null ) && !listRecordField.isEmpty(  ) )
        {
            recordField = listRecordField.get( 0 );
        }

        return recordField;
    }

    /**
     * Get the file name from a given url
     * @param strUrl the url
     * @return the file name
     * @throws HttpAccessException Exception if there is an HTTP issue
     */
    public String getFileName( String strUrl ) throws HttpAccessException
    {
        return _blobStoreClientWS.getFileName( strUrl );
    }

    /**
     * Get the file name from a given edit record and id entry
     * @param editRecord the edit record
     * @param nIdEntry the id entry
     * @return the file name
     */
    public String getFileName( EditRecord editRecord, int nIdEntry )
    {
        String strFileName = StringUtils.EMPTY;

        for ( EditRecordValue editRecordValue : editRecord.getListEditRecordValues(  ) )
        {
            if ( editRecordValue.getIdEntry(  ) == nIdEntry )
            {
                strFileName = editRecordValue.getFileName(  );

                break;
            }
        }

        return strFileName;
    }

    /**
     * Get the WS rest url of the given entry
     * @param entry the entry
     * @return the WS rest url
     */
    public String getWSRestUrl( IEntry entry )
    {
        return getFieldValue( entry, EntryTypeDownloadUrl.CONSTANT_WS_REST_URL );
    }

    /**
     * Get the BlobStore service name of the given entry
     * @param entry the entry
     * @return the BlobStore service name
     */
    public String getBlobStoreName( IEntry entry )
    {
        return getFieldValue( entry, EntryTypeDownloadUrl.CONSTANT_BLOBSTORE );
    }

    /**
     * Get the file url
     * @param strBaseUrl the base url
     * @param strBlobKey the blob key
     * @param strBlobStore the blobstore service name
     * @return the file url
     * @throws HttpAccessException Exception if there is an HTTP issue
     */
    public String getFileUrl( String strBaseUrl, String strBlobKey, String strBlobStore )
        throws HttpAccessException
    {
        return _blobStoreClientWS.getFileUrl( strBaseUrl, strBlobKey, strBlobStore );
    }

    // DO

    /**
     * Do edit the record data
     * @param request the HTTP request
     * @param editRecord the edit record
     * @throws SiteMessageException site message if there is a problem
     */
    public void doEditRecordData( HttpServletRequest request, EditRecord editRecord )
        throws SiteMessageException
    {
        Plugin pluginDirectory = PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME );

        Record record = getRecordFromIdHistory( editRecord.getIdHistory(  ) );

        if ( record != null )
        {
            List<IEntry> listEntriesToEdit = getListEntriesToEdit( request, editRecord.getListEditRecordValues(  ) );
            List<RecordField> listRecordFields = getListRecordFieldsToNotEdit( request, record.getIdRecord(  ),
                    editRecord.getListEditRecordValues(  ) );

            try
            {
                for ( IEntry entry : listEntriesToEdit )
                {
                    getRecordFieldData( request, entry, record, listRecordFields, pluginDirectory, editRecord );
                }

                record.setListRecordField( listRecordFields );
            }
            catch ( DirectoryErrorException error )
            {
                if ( error.isMandatoryError(  ) )
                {
                    Object[] tabRequiredFields = { error.getTitleField(  ) };
                    SiteMessageService.setMessage( request, EditRecordConstants.MESSAGE_MANDATORY_FIELD,
                        tabRequiredFields, SiteMessage.TYPE_STOP );
                }
                else
                {
                    Object[] tabRequiredFields = { error.getTitleField(  ), error.getErrorMessage(  ) };
                    SiteMessageService.setMessage( request, EditRecordConstants.MESSAGE_DIRECTORY_ERROR,
                        tabRequiredFields, SiteMessage.TYPE_STOP );
                }
            }

            RecordHome.updateWidthRecordField( record, pluginDirectory );
        }
        else
        {
            setSiteMessage( request, EditRecordConstants.MESSAGE_APP_ERROR, SiteMessage.TYPE_STOP,
                request.getParameter( EditRecordConstants.PARAMETER_URL_RETURN ) );
        }
    }

    /**
     * Do change the record state
     * @param editRecord edit record
     * @param locale the locale
     */
    public void doChangeRecordState( EditRecord editRecord, Locale locale )
    {
        Plugin pluginWorkflow = PluginService.getPlugin( WorkflowPlugin.PLUGIN_NAME );
        ITask task = TaskHome.findByPrimaryKey( editRecord.getIdTask(  ), pluginWorkflow, locale );
        TaskEditRecordConfig config = TaskEditRecordConfigService.getService(  )
                                                                 .findByPrimaryKey( editRecord.getIdTask(  ) );

        if ( ( task != null ) && ( config != null ) )
        {
            State state = StateHome.findByPrimaryKey( config.getIdStateAfterEdition(  ), pluginWorkflow );
            Action action = ActionHome.findByPrimaryKey( task.getAction(  ).getId(  ), pluginWorkflow );

            if ( ( state != null ) && ( action != null ) )
            {
                Record record = getRecordFromIdHistory( editRecord.getIdHistory(  ) );

                // Create Resource History
                ResourceHistory resourceHistory = new ResourceHistory(  );
                resourceHistory.setIdResource( record.getIdRecord(  ) );
                resourceHistory.setResourceType( Record.WORKFLOW_RESOURCE_TYPE );
                resourceHistory.setAction( action );
                resourceHistory.setWorkFlow( action.getWorkflow(  ) );
                resourceHistory.setCreationDate( WorkflowUtils.getCurrentTimestamp(  ) );
                resourceHistory.setUserAccessCode( EditRecordConstants.USER_AUTO );
                ResourceHistoryHome.create( resourceHistory, pluginWorkflow );

                // Update Resource
                ResourceWorkflow resourceWorkflow = ResourceWorkflowHome.findByPrimaryKey( record.getIdRecord(  ),
                        Record.WORKFLOW_RESOURCE_TYPE, action.getWorkflow(  ).getId(  ), pluginWorkflow );
                resourceWorkflow.setState( state );
                ResourceWorkflowHome.update( resourceWorkflow, pluginWorkflow );

                // if new state have action automatic
                WorkflowService.getInstance(  )
                               .executeActionAutomatic( record.getIdRecord(  ), Record.WORKFLOW_RESOURCE_TYPE,
                    action.getWorkflow(  ).getId(  ), resourceWorkflow.getExternalParentId(  ) );
            }
        }
    }

    /**
     * Do change the edit record to complete
     * @param editRecord the edit record
     */
    public void doCompleteEditRecord( EditRecord editRecord )
    {
        editRecord.setIsComplete( true );
        update( editRecord );
    }

    /**
     * Do remove a file from a given edit record and entry
     * @param editRecord the edit record
     * @param entry the entry
     * @throws HttpAccessException Exception if there is an HTTP issue
     */
    public void doRemoveFile( EditRecord editRecord, IEntry entry )
        throws HttpAccessException
    {
        for ( EditRecordValue editRecordValue : editRecord.getListEditRecordValues(  ) )
        {
            if ( editRecordValue.getIdEntry(  ) == entry.getIdEntry(  ) )
            {
                doRemoveFile( editRecordValue, entry );

                break;
            }
        }
    }

    /**
     * Do edit the value of the record field
     * @param nIdHistory the id history
     * @param nIdEntry the id entry
     * @param strRecordFieldValue the record field value
     */
    public void doEditRecordFieldDownloadUrl( int nIdHistory, int nIdEntry, String strRecordFieldValue )
    {
        RecordField recordField = getRecordFieldDownloadUrl( nIdHistory, nIdEntry );

        if ( recordField != null )
        {
            Plugin pluginDirectory = PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME );
            recordField.setValue( strRecordFieldValue );
            RecordFieldHome.update( recordField, pluginDirectory );
        }
    }

    /**
     * Do upload a file in the blobstore webapp
     * @param strBaseUrl the base url
     * @param fileItem the file
     * @param strBlobStore the blobstore service name
     * @return the blob key of the uploaded file
     * @throws HttpAccessException Exception if there is an HTTP issue
     */
    public String doUploadFile( String strBaseUrl, FileItem fileItem, String strBlobStore )
        throws HttpAccessException
    {
        return _blobStoreClientWS.doUploadFile( strBaseUrl, fileItem, strBlobStore );
    }

    // CHECK

    /**
     * Check if the request is authenticated or not
     * @param request the HTTP request
     * @return true if the requet is authenticated, false otherwise
     */
    public boolean isRequestAuthenticated( HttpServletRequest request )
    {
        return EditRecordRequestAuthenticatorService.getRequestAuthenticator(  ).isRequestAuthenticated( request );
    }

    // PRIVATE METHODS

    /**
     * Get the field value of the entry from a given field title
     * @param entry the entry
     * @param strFieldTitle field title
     * @return the field value
     */
    private String getFieldValue( IEntry entry, String strFieldTitle )
    {
        String strFieldValue = StringUtils.EMPTY;

        if ( entry instanceof EntryTypeDownloadUrl && ( entry.getFields(  ) != null ) &&
                !entry.getFields(  ).isEmpty(  ) && StringUtils.isNotBlank( strFieldTitle ) )
        {
            for ( Field field : entry.getFields(  ) )
            {
                if ( strFieldTitle.equals( field.getTitle(  ) ) )
                {
                    strFieldValue = field.getValue(  );

                    break;
                }
            }
        }

        return strFieldValue;
    }

    /**
     * Get the record field data
     * @param request the HttpServletRequest
     * @param entry the entry
     * @param record the record
     * @param listRecordFields the list of record fields
     * @param pluginDirectory the plugin directory
     * @param editRecord the edit record
     * @throws DirectoryErrorException Directory exception if mandatory fields are not filled
     */
    private void getRecordFieldData( HttpServletRequest request, IEntry entry, Record record,
        List<RecordField> listRecordFields, Plugin pluginDirectory, EditRecord editRecord )
        throws DirectoryErrorException
    {
        if ( entry instanceof EntryTypeDownloadUrl )
        {
            // If entry type download url, then the file is already uploaded, so no need to fetch "again"
            // otherwise, it will fetch an empty string
            RecordField recordField = getRecordFieldDownloadUrl( editRecord.getIdHistory(  ), entry.getIdEntry(  ) );

            if ( entry.isMandatory(  ) && ( ( recordField == null ) || StringUtils.isBlank( recordField.getValue(  ) ) ) )
            {
                throw new DirectoryErrorException( entry.getTitle(  ) );
            }

            listRecordFields.add( recordField );
        }
        else
        {
            DirectoryUtils.getDirectoryRecordFieldData( record, request, entry.getIdEntry(  ), true, listRecordFields,
                pluginDirectory, request.getLocale(  ) );
        }
    }

    /**
     * Do remove a file
     * @param editRecordValue the edit record value
     * @param entry the entry
     * @throws HttpAccessException Exception if there is an HTTP issue
     */
    private void doRemoveFile( EditRecordValue editRecordValue, IEntry entry )
        throws HttpAccessException
    {
        RecordField recordField = getRecordFieldDownloadUrl( editRecordValue.getIdHistory(  ), entry.getIdEntry(  ) );

        if ( recordField != null )
        {
            // Get the download file url
            String strDownloadFileUrl = entry.convertRecordFieldTitleToString( recordField, null, false );

            if ( StringUtils.isNotBlank( strDownloadFileUrl ) )
            {
                // Parse the download file url to fetch the parameters
                Map<String, List<String>> mapParameters = UrlUtils.getMapParametersFromUrl( strDownloadFileUrl );
                List<String> parameterBlobKey = mapParameters.get( EditRecordConstants.PARAMETER_BLOB_KEY );
                List<String> parameterBlobStore = mapParameters.get( EditRecordConstants.PARAMETER_BLOBSTORE );

                if ( ( parameterBlobKey != null ) && !parameterBlobKey.isEmpty(  ) && ( parameterBlobStore != null ) &&
                        !parameterBlobStore.isEmpty(  ) )
                {
                    String strBlobKey = parameterBlobKey.get( 0 );
                    String strBlobStore = parameterBlobStore.get( 0 );
                    _blobStoreClientWS.doDeleteFile( getWSRestUrl( entry ), strBlobStore, strBlobKey );

                    // Update the record field
                    doEditRecordFieldDownloadUrl( editRecordValue.getIdHistory(  ), entry.getIdEntry(  ),
                        StringUtils.EMPTY );
                }
            }
        }
    }
}
