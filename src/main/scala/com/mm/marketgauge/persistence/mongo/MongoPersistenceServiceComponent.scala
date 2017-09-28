package com.mm.marketgauge.persistence.mongo
import com.mm.marketgauge.persistence.PersistenceServiceComponent

trait MongoPersistenceServiceComponent extends PersistenceServiceComponent {
  
  override val sectorRepository = new SectorRepository{}
  
  override val companyRepository = new CompanyRepository{}
  
  override val companyRepoRepository = new CompanyRepoRepository {}
  
  override val sharePriceRepository = new SharePriceRepository{}
    
}


