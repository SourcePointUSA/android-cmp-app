package com.sourcepointmeta.metaapp.viewmodel;

import android.arch.core.executor.testing.InstantTaskExecutorRule;

import com.sourcepointmeta.metaapp.database.AppDataBase;
import com.sourcepointmeta.metaapp.database.dao.PropertyListDao;
import com.sourcepointmeta.metaapp.database.dao.TargetingParamDao;
import com.sourcepointmeta.metaapp.repository.PropertyListRepository;

import org.junit.Before;
import org.junit.Rule;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ConsentViewViewModelTest {

    private final PropertyListDao propertyListDao = mock(PropertyListDao.class);
    private final TargetingParamDao targetingParamDao = mock(TargetingParamDao.class);
    private PropertyListRepository propertyListRepository;
    private ConsentViewViewModel viewModel ;

    @Before
    public void getViewModel(){

        AppDataBase appDataBase = mock(AppDataBase.class);
        when(appDataBase.propertyListDao()).thenReturn(propertyListDao);
        when(appDataBase.targetingParamDao()).thenReturn(targetingParamDao);
        propertyListRepository = mock(PropertyListRepository.class);

        viewModel =  new ConsentViewViewModel(propertyListRepository);

    }

    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();


}