package com.mm.marketgauge.persistence

import com.mm.marketgauge.entities.{ Sector, Company, CompanyRepo, SharePrice }
import com.mm.marketgauge.dao._

/**
 * Global Persistence Service for the application
 * // Need to be refactored. database will be injected
 */


trait PersistenceServiceComponent {

  // This needs better abstractions....
  
  val sectorRepository : BaseRepository[Sector]
  
  val companyRepository: BaseRepository[Company]
  
  val companyRepoRepository: BaseRepository[CompanyRepo]
  
  val sharePriceRepository: BaseRepository[SharePrice]
    
}




